/**
 * 가족 트리 시각화 관련 타입 정의
 */

import { FamilyMember, FamilyMemberRelationship } from './family';

export interface FamilyTreeNode {
  id: string;
  member: FamilyMember;
  children: FamilyTreeNode[];
  parents: FamilyTreeNode[];
  relationships: FamilyMemberRelationship[];
  level: number;
  generation: number;
}

export interface FamilyTreeGeneration {
  level: number;
  members: FamilyTreeNode[];
}

export interface FamilyTreeRelation {
  fromNodeId: string;
  toNodeId: string;
  relationshipType: string;
  customRelationship?: string;
}

export interface FamilyTreeMetadata {
  totalMembers: number;
  generations: number;
  maxGenerationSize: number;
  rootMemberId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface FamilyTree {
  familyId: string;
  rootNode?: FamilyTreeNode;
  generations: FamilyTreeGeneration[];
  relationships: FamilyTreeRelation[];
  metadata: FamilyTreeMetadata;
}