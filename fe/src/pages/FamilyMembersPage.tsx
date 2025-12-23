import React from 'react';
import { useParams } from 'react-router-dom';
import { FamilyMemberCard } from '../components/family/FamilyMemberCard';
import { Button } from '../components/common/Button';
import { Card, CardContent, CardHeader } from '../components/common/Card';
import { useFamilyMembers, useFamilyDetail } from '../hooks/queries/useFamilyQueries';
import { FamilyMemberWithRelationship } from '../api/services/familyService';

const FamilyMembersPage: React.FC = () => {
  const { familyId } = useParams<{ familyId: string }>();
  const familyIdNumber = familyId ? parseInt(familyId, 10) : undefined;
  const { data: familyData, isLoading: familyLoading } = useFamilyDetail(familyIdNumber!);
  const { data: membersData, isLoading: membersLoading, isError } = useFamilyMembers(familyIdNumber!);

  const isLoading = familyLoading || membersLoading;
  const members = membersData || [];

  const handleMemberClick = (member: FamilyMemberWithRelationship) => {
    // TODO: 멤버 상세 모달 열기
    console.log('Member clicked:', member);
  };

  const handleRelationshipEdit = (member: FamilyMemberWithRelationship) => {
    // TODO: 관계 설정 모달 열기
    console.log('Edit relationship for:', member);
  };

  if (!familyId) {
    return (
      <div className="container">
        <div className="text-center py-20">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">그룹 ID가 없습니다</h2>
          <p className="text-gray-600">올바른 그룹 페이지로 이동해주세요.</p>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="container">
        <div className="text-center py-20">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">멤버 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="container">
        <div className="text-center py-20">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">오류가 발생했습니다</h2>
          <p className="text-gray-600 mb-6">멤버 정보를 불러올 수 없습니다.</p>
          <Button onClick={() => window.location.reload()}>다시 시도</Button>
        </div>
      </div>
    );
  }

  // 이름순으로 정렬
  const sortedMembers = [...members].sort((a, b) => a.memberName.localeCompare(b.memberName));

  return (
    <div className="container">
      <div className="py-6">
        {/* 헤더 */}
        <Card className="mb-6">
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">
                  {familyData?.name || '우리 그룹'}
                </h1>
                <p className="text-gray-600">
                  멤버 {members.length}명
                </p>
              </div>
              <div className="flex gap-2">
                <Button variant="outline" size="sm">
                  설정
                </Button>
                <Button variant="primary" size="sm">
                  멤버 추가
                </Button>
              </div>
            </div>
          </CardHeader>
        </Card>

        {/* 멤버 목록 */}
        {members.length === 0 ? (
          <Card className="text-center">
            <CardContent>
              <div className="py-12">
                <div className="text-6xl mb-4">👥</div>
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  아직 멤버가 없습니다
                </h3>
                <p className="text-gray-600 mb-6">
                  첫 번째 멤버를 추가해보세요
                </p>
                <Button variant="primary" size="lg">
                  멤버 추가하기
                </Button>
              </div>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">
                멤버 목록
              </h2>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <span>이름순 정렬</span>
              </div>
            </div>

            <div className="space-y-3">
              {sortedMembers.map((memberWithRelationship) => (
                <FamilyMemberCard
                  key={memberWithRelationship.memberId}
                  memberWithRelationship={memberWithRelationship}
                  onMemberClick={handleMemberClick}
                  onRelationshipEdit={handleRelationshipEdit}
                />
              ))}
            </div>
          </div>
        )}

        {/* 통계 정보 */}
        {members.length > 0 && (
          <Card className="mt-8">
            <CardHeader>
              <h3 className="text-lg font-semibold text-gray-900">통계</h3>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-4 text-center">
                <div>
                  <div className="text-2xl font-bold text-orange-500">
                    {members.length}
                  </div>
                  <div className="text-sm text-gray-600">총 멤버</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-orange-500">
                    {members.filter(m => m.member.status === 'ACTIVE').length}
                  </div>
                  <div className="text-sm text-gray-600">활성 멤버</div>
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* TODO: 모달들 */}
        {/* 멤버 상세 모달, 관계 설정 모달 등 */}
      </div>
    </div>
  );
};

export default FamilyMembersPage;
