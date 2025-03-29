import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { FamilyTreeService } from "../../api/services/familyTree";
import {
  CreateFamilyMemberDto,
  UpdateFamilyMemberDto,
} from "../../types/familyTree";

export const familyTreeKeys = {
  all: ["familyTree"] as const,
  members: () => [...familyTreeKeys.all, "members"] as const,
  member: (id: string) => [...familyTreeKeys.all, "member", id] as const,
};

export function useFamilyTree() {
  return useQuery({
    queryKey: familyTreeKeys.all,
    queryFn: () => FamilyTreeService.getInstance().getFamily(),
  });
}

export function useFamilyMember(id: string) {
  return useQuery({
    queryKey: familyTreeKeys.member(id),
    queryFn: () => FamilyTreeService.getInstance().getFamilyMember(id),
    enabled: !!id,
  });
}

export function useFamilyMembers(page: number = 0, size: number = 20) {
  return useQuery({
    queryKey: [...familyTreeKeys.members(), { page, size }],
    queryFn: () => FamilyTreeService.getInstance().getFamilyMembers(page, size),
  });
}

export function useCreateFamilyMember() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (member: CreateFamilyMemberDto) =>
      FamilyTreeService.getInstance().createFamilyMember(member),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: familyTreeKeys.all });
    },
  });
}

export function useUpdateFamilyMember() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (member: UpdateFamilyMemberDto) =>
      FamilyTreeService.getInstance().updateFamilyMember(member),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: familyTreeKeys.all });
      queryClient.invalidateQueries({
        queryKey: familyTreeKeys.member(data.id),
      });
    },
  });
}

export function useDeleteFamilyMember() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) =>
      FamilyTreeService.getInstance().deleteFamilyMember(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: familyTreeKeys.all });
      queryClient.invalidateQueries({ queryKey: familyTreeKeys.member(id) });
    },
  });
}
