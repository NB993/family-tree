import React from 'react';
import { Phone, Heart } from 'lucide-react';
import { Button } from '../../common/Button';
import { Avatar, AvatarFallback, AvatarImage } from '../../ui/avatar';
import { cn } from '../../../design-system/utils';
import { FamilyMemberWithRelationship } from '../../../api/services/familyService';
import { FamilyMemberRelationshipType } from '../../../types/family';

interface NewFamilyMemberCardProps {
  memberWithRelationship: FamilyMemberWithRelationship;
  onRelationshipEdit: (member: FamilyMemberWithRelationship) => void;
  onMemberClick: (member: FamilyMemberWithRelationship) => void;
}

export function FamilyMemberCard({
  memberWithRelationship,
  onRelationshipEdit,
  onMemberClick,
}: NewFamilyMemberCardProps) {
  const getRelationshipDisplay = () => {
    if (!memberWithRelationship.hasRelationship) {
      return "관계를 설정해주세요";
    }
    if (memberWithRelationship.relationshipType === FamilyMemberRelationshipType.CUSTOM && memberWithRelationship.customRelationshipName) {
      return memberWithRelationship.customRelationshipName;
    }
    return memberWithRelationship.relationshipDisplayName || "관계 정보 없음";
  };

  const getContactDisplay = () => {
    if (!memberWithRelationship.memberPhoneNumber) {
      return "연락처 없음";
    }
    // TODO: 카카오톡 동기화 상태는 백엔드에서 제공될 예정
    return formatPhoneNumber(memberWithRelationship.memberPhoneNumber);
  };

  const formatPhoneNumber = (phone: string): string => {
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
      return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    }
    if (cleaned.length === 10) {
      return cleaned.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3');
    }
    return phone;
  };

  const formatBirthDate = (dateString?: string): string => {
    if (!dateString) return '생년월일 없음';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  const getRelationshipEmoji = (): string => {
    if (!memberWithRelationship.hasRelationship) return "❓";

    const emojiMap: Record<string, string> = {
      [FamilyMemberRelationshipType.FATHER]: "👨",
      [FamilyMemberRelationshipType.MOTHER]: "👩",
      [FamilyMemberRelationshipType.GRANDFATHER]: "👴",
      [FamilyMemberRelationshipType.GRANDMOTHER]: "👵",
      [FamilyMemberRelationshipType.ELDER_BROTHER]: "👨‍🦱",
      [FamilyMemberRelationshipType.ELDER_SISTER]: "👩‍🦱",
      [FamilyMemberRelationshipType.YOUNGER_BROTHER]: "👦",
      [FamilyMemberRelationshipType.YOUNGER_SISTER]: "👧",
      [FamilyMemberRelationshipType.UNCLE]: "👨‍🦲",
      [FamilyMemberRelationshipType.AUNT]: "👩‍🦲",
      [FamilyMemberRelationshipType.COUSIN]: "🧑",
      [FamilyMemberRelationshipType.HUSBAND]: "🤵",
      [FamilyMemberRelationshipType.WIFE]: "👰",
      [FamilyMemberRelationshipType.SON]: "👦",
      [FamilyMemberRelationshipType.DAUGHTER]: "👧",
    };

    return emojiMap[memberWithRelationship.relationshipType as FamilyMemberRelationshipType] || "👤";
  };

  return (
    <div className="family-card p-4 mb-4 group">
      <div className="flex items-start gap-4">
        <div className="cursor-pointer" onClick={() => onMemberClick(memberWithRelationship)}>
          <div className="relative">
            <Avatar className="w-14 h-14 ring-2 ring-orange-100 group-hover:ring-orange-300 transition-all duration-300">
              <AvatarImage src={memberWithRelationship.memberProfileImageUrl} alt={memberWithRelationship.memberName} />
              <AvatarFallback className="bg-family-gradient text-white text-lg font-semibold">
                {getRelationshipEmoji()}
              </AvatarFallback>
            </Avatar>
            <div className="absolute -bottom-1 -right-1 w-6 h-6 bg-orange-100 rounded-full flex items-center justify-center">
              <Heart className="w-3 h-3 text-orange-500" />
            </div>
          </div>
        </div>

        <div className="flex-1 min-w-0">
          <div className="cursor-pointer" onClick={() => onMemberClick(memberWithRelationship)}>
            <div className="flex items-center gap-2 mb-2">
              <h3 className="text-h6 font-bold text-gray-900">{memberWithRelationship.memberName}</h3>
              <span
                className={cn(
                  "text-caption px-2 py-1 rounded-full font-medium",
                  !memberWithRelationship.hasRelationship
                    ? "text-amber-700 bg-amber-100 border border-amber-200"
                    : "text-orange-700 bg-orange-100 border border-orange-200",
                )}
              >
                {getRelationshipDisplay()}
              </span>
            </div>

            {memberWithRelationship.memberAge && (
              <div className="text-body2 text-gray-600 mb-2 flex items-center gap-2">
                <span className="font-medium">{memberWithRelationship.memberAge}세</span>
                <span className="text-gray-400">•</span>
                <span>{formatBirthDate(memberWithRelationship.memberBirthday)}</span>
              </div>
            )}

            <div className="flex items-center gap-2 text-body2 mb-4">
              <Phone className="w-4 h-4 text-gray-400" />
              <span
                className={cn(
                  "font-medium",
                  !memberWithRelationship.memberPhoneNumber
                    ? "text-gray-400"
                    : "text-gray-600",
                )}
              >
                {getContactDisplay()}
              </span>
            </div>
          </div>

          <div className="flex justify-end">
            <Button
              onClick={() => onRelationshipEdit(memberWithRelationship)}
              variant="secondary"
              size="sm"
              className="text-caption h-9 px-4 shadow-family-sm"
            >
              <Heart className="w-3 h-3 mr-1" />
              관계 설정
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export interface FamilyMemberCardProps {
  member: any; // 기존 호환성을 위한 임시 인터페이스
  onMemberClick?: (member: any) => void;
  onRelationshipEdit?: (member: any) => void;
  showRelationshipButton?: boolean;
  clickable?: boolean;
}