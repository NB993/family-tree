import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { useFamilyDetail, useFamilyMembers, useFamilyAnnouncements } from '../hooks/queries/useFamilyQueries';
import { useAuth } from '../contexts/AuthContext';

const FamilyPage: React.FC = () => {
  const { familyId } = useParams<{ familyId: string }>();
  const familyIdNumber = familyId ? parseInt(familyId, 10) : undefined;
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  
  const { data: familyData, isLoading: familyLoading } = useFamilyDetail(familyIdNumber!);
  const { data: membersData, isLoading: membersLoading } = useFamilyMembers(familyIdNumber!);
  const { data: announcements, isLoading: announcementsLoading } = useFamilyAnnouncements(familyIdNumber!);

  const isLoading = familyLoading || membersLoading || announcementsLoading;
  const members = membersData || [];

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
          <p className="mt-4 text-gray-600">그룹 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="py-6">
        {/* 가족 정보 헤더 */}
        <Card className="mb-6">
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-2xl font-bold text-gray-900 mb-2">
                  {familyData?.name || '우리 그룹'}
                </h1>
                <p className="text-gray-600">
                  {familyData?.description || '그룹에 대한 설명이 없습니다'}
                </p>
                <div className="flex items-center gap-2 mt-2">
                  <span className="text-sm text-gray-500">멤버 {members.length}명</span>
                  {familyData?.isPublic ? (
                    <span className="px-2 py-1 text-xs bg-green-100 text-green-700 rounded-full">
                      공개
                    </span>
                  ) : (
                    <span className="px-2 py-1 text-xs bg-gray-100 text-gray-700 rounded-full">
                      비공개
                    </span>
                  )}
                </div>
              </div>
              {isAuthenticated ? (
                <Button variant="outline">
                  설정
                </Button>
              ) : (
                <Button 
                  variant="outline"
                  onClick={() => navigate('/login')}
                >
                  로그인하여 참여
                </Button>
              )}
            </div>
          </CardHeader>
        </Card>

        {/* 메뉴 카드들 */}
        <div className="grid grid-cols-2 gap-4 mb-6">
          <Link to={`/families/${familyId}/members`}>
            <Card clickable>
              <CardContent>
                <div className="text-center py-6">
                  <div className="text-3xl mb-2">👥</div>
                  <h3 className="font-semibold text-gray-900 mb-1">멤버</h3>
                  <p className="text-sm text-gray-600">{members.length}명</p>
                </div>
              </CardContent>
            </Card>
          </Link>

          <Card clickable>
            <CardContent>
              <div className="text-center py-6">
                <div className="text-3xl mb-2">📞</div>
                <h3 className="font-semibold text-gray-900 mb-1">연락처</h3>
                <p className="text-sm text-gray-600">전화번호 관리</p>
              </div>
            </CardContent>
          </Card>

          <Card clickable>
            <CardContent>
              <div className="text-center py-6">
                <div className="text-3xl mb-2">📅</div>
                <h3 className="font-semibold text-gray-900 mb-1">일정</h3>
                <p className="text-sm text-gray-600">그룹 일정</p>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 최근 멤버 */}
        {members.length > 0 && (
          <Card className="mb-6">
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-900">최근 멤버</h3>
                <Link to={`/families/${familyId}/members`}>
                  <Button variant="ghost" size="sm">
                    전체 보기
                  </Button>
                </Link>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {members.slice(0, 3).map((memberWithRelationship) => (
                  <div key={memberWithRelationship.memberId} className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center">
                      <span className="text-lg">
                        {memberWithRelationship.memberName.charAt(0)}
                      </span>
                    </div>
                    <div className="flex-1">
                      <div className="font-medium text-gray-900">{memberWithRelationship.memberName}</div>
                      <div className="text-sm text-gray-600">
                        {memberWithRelationship.relationshipGuideMessage} · {memberWithRelationship.member.role}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        {/* 공지사항 */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">공지사항</h3>
              <Button variant="ghost" size="sm">
                더보기
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            {announcements && announcements.length > 0 ? (
              <div className="space-y-3">
                {announcements.slice(0, 3).map((announcement) => (
                  <div key={announcement.id} className="border-l-4 border-orange-200 pl-3">
                    <div className="flex items-start justify-between">
                      <div>
                        <h4 className="font-medium text-gray-900 mb-1">
                          {announcement.title}
                        </h4>
                        <p className="text-sm text-gray-600 line-clamp-2">
                          {announcement.content}
                        </p>
                        <p className="text-xs text-gray-500 mt-1">
                          {new Date(announcement.createdAt).toLocaleDateString()}
                        </p>
                      </div>
                      {announcement.isImportant && (
                        <span className="px-2 py-1 text-xs bg-red-100 text-red-700 rounded-full">
                          중요
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <div className="text-4xl mb-2">📢</div>
                <p className="text-gray-600">아직 공지사항이 없습니다</p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default FamilyPage;