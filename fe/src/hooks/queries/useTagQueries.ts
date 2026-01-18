/**
 * 태그 관련 React Query 훅들
 * PRD-006: FamilyMember 태그 기능
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { TagService } from '../../api/services/tagService';
import {
  Tag,
  SaveTagRequest,
  ModifyTagRequest,
  MemberTagsResponse,
} from '../../types/tag';
import { familyQueryKeys } from './useFamilyQueries';

const tagService = TagService.getInstance();

// Query Keys
export const tagQueryKeys = {
  all: ['tags'] as const,
  lists: () => [...tagQueryKeys.all, 'list'] as const,
  list: (familyId: number | string) => [...tagQueryKeys.lists(), familyId.toString()] as const,
} as const;

/**
 * 태그 목록을 조회합니다.
 */
export const useTags = (familyId: number | string) => {
  return useQuery({
    queryKey: tagQueryKeys.list(familyId),
    queryFn: () => tagService.findTags(familyId),
    enabled: !!familyId,
    staleTime: 2 * 60 * 1000, // 2분
  });
};

/**
 * 새로운 태그를 생성합니다.
 */
export const useSaveTag = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ familyId, request }: { familyId: number | string; request: SaveTagRequest }) =>
      tagService.saveTag(familyId, request),
    onSuccess: (_, { familyId }) => {
      // 태그 목록 무효화
      queryClient.invalidateQueries({
        queryKey: tagQueryKeys.list(familyId),
      });
    },
  });
};

/**
 * 태그를 수정합니다.
 */
export const useModifyTag = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      familyId,
      tagId,
      request,
    }: {
      familyId: number | string;
      tagId: number | string;
      request: ModifyTagRequest;
    }) => tagService.modifyTag(familyId, tagId, request),
    onSuccess: (_, { familyId }) => {
      // 태그 목록 무효화
      queryClient.invalidateQueries({
        queryKey: tagQueryKeys.list(familyId),
      });
      // 멤버 목록도 무효화 (태그 정보 포함)
      queryClient.invalidateQueries({
        queryKey: familyQueryKeys.members(familyId),
      });
    },
  });
};

/**
 * 태그를 삭제합니다.
 */
export const useDeleteTag = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ familyId, tagId }: { familyId: number | string; tagId: number | string }) =>
      tagService.deleteTag(familyId, tagId),
    onSuccess: (_, { familyId }) => {
      // 태그 목록 무효화
      queryClient.invalidateQueries({
        queryKey: tagQueryKeys.list(familyId),
      });
      // 멤버 목록도 무효화 (태그 정보 포함)
      queryClient.invalidateQueries({
        queryKey: familyQueryKeys.members(familyId),
      });
    },
  });
};

/**
 * 멤버에 태그를 할당합니다.
 */
export const useModifyMemberTags = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      familyId,
      memberId,
      tagIds,
    }: {
      familyId: number | string;
      memberId: number | string;
      tagIds: number[];
    }) => tagService.modifyMemberTags(familyId, memberId, tagIds),
    onSuccess: (_, { familyId }) => {
      // 태그 목록 무효화 (멤버 수 변경)
      queryClient.invalidateQueries({
        queryKey: tagQueryKeys.list(familyId),
      });
      // 멤버 목록 무효화 (태그 정보 포함)
      queryClient.invalidateQueries({
        queryKey: familyQueryKeys.members(familyId),
      });
    },
  });
};