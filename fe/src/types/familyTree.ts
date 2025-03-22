export interface FamilyMember {
  id: string;
  name: string;
  birthDate: string;
  death: boolean;
  deathDate?: string;
  //   gender: "MALE" | "FEMALE";
  parentId?: string;
  spouseId?: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Family {
  members: FamilyMember[];
  relationships: {
    parentChild: Array<{
      parentId: string;
      childId: string;
      relationshipType: "BIOLOGICAL" | "ADOPTED";
    }>;
    marriage: Array<{
      spouse1Id: string;
      spouse2Id: string;
      marriageDate?: string;
      divorceDate?: string;
    }>;
  };
}

export interface CreateFamilyMemberDto {
  name: string;
  birthDate: string;
  gender: "MALE" | "FEMALE";
  parentId?: string;
  spouseId?: string;
  description?: string;
}

export interface UpdateFamilyMemberDto extends Partial<CreateFamilyMemberDto> {
  id: string;
}
