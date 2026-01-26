/**
 * 사용자 관련 React Query 훅들
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { UserService } from '../../api/services/user';
import { ModifyUserRequest } from '../../types/user';

const userService = UserService.getInstance();

// Query Keys
export const userQueryKeys = {
  all: ['users'] as const,
  current: () => [...userQueryKeys.all, 'current'] as const,
} as const;

/**
 * 현재 로그인한 사용자 정보를 조회합니다.
 */
export const useCurrentUser = () => {
  return useQuery({
    queryKey: userQueryKeys.current(),
    queryFn: () => userService.getCurrentUser(),
    staleTime: 5 * 60 * 1000, // 5분
  });
};

/**
 * 사용자 프로필을 수정합니다.
 */
export const useModifyUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: ModifyUserRequest) => userService.modifyUser(request),
    onSuccess: () => {
      // 현재 사용자 정보 무효화
      queryClient.invalidateQueries({
        queryKey: userQueryKeys.current(),
      });
    },
  });
};
