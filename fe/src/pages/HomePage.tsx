import React, { useState, useMemo, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMyFamilies, useFamilyMembers } from '../hooks/queries/useFamilyQueries';
import { FamilyMemberWithRelationship } from '../api/services/familyService';
import { MoreVertical } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

const HomePage: React.FC = () => {
  const { data: familiesData, isLoading, isError } = useMyFamilies();
  const navigate = useNavigate();
  const { userInfo, logout } = useAuth();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFamilyId, setSelectedFamilyId] = useState<number | null>(null);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsMenuOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [menuRef]);

  // 선택된 가족의 구성원 데이터 가져오기
  const { data: membersData } = useFamilyMembers(selectedFamilyId || 0);

  // 첫 번째 가족을 기본 선택
  useEffect(() => {
    if (familiesData && familiesData.length > 0 && !selectedFamilyId) {
      setSelectedFamilyId(familiesData[0].id);
    }
  }, [familiesData, selectedFamilyId]);

  // 검색 필터링
  const filteredMembers = useMemo(() => {
    if (!membersData || !searchTerm) return membersData || [];
    
    return membersData.filter((member: FamilyMemberWithRelationship) => 
      member.memberName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      member.memberPhoneNumber?.includes(searchTerm) ||
      member.phoneNumberDisplay?.includes(searchTerm)
    );
  }, [membersData, searchTerm]);

  const families = familiesData || [];
  const hasData = families.length > 0 && membersData && membersData.length > 0;

  return (
    <>
      {/* 헤더 영역 */}
      <header className="flex justify-between items-center mb-8">
        <div className="flex items-center gap-4 min-w-0">
          <h1 className="text-2xl sm:text-3xl font-bold text-gray-900 flex items-center gap-2">
            <span className="flex-shrink-0">🏠</span>
            <span>Family Tree</span>
          </h1>
          <span className="hidden sm:inline text-gray-600 truncate">
            {userInfo ? `${userInfo.name}님의 가족` : '가족 트리'}
          </span>
        </div>
        
        {/* Mobile Dropdown Menu */}
        <div className="relative sm:hidden" ref={menuRef}>
          <button
            onClick={() => setIsMenuOpen(prev => !prev)}
            className="p-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <MoreVertical className="h-5 w-5 text-gray-600" />
          </button>

          {isMenuOpen && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10 border border-gray-200">
              {/* <button
                onClick={() => {
                  navigate('/profile');
                  setIsMenuOpen(false);
                }}
                className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                프로필
              </button>
              <button
                onClick={() => {
                  navigate('/settings');
                  setIsMenuOpen(false);
                }}
                className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                설정
              </button>
              <div className="border-t my-1"></div> */}
              <button
                onClick={logout}
                className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
              >
                로그아웃
              </button>
            </div>
          )}
        </div>

        {/* Desktop Buttons */}
        <div className="hidden sm:flex items-center gap-3">
            {/* <button
              onClick={() => navigate('/profile')}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors text-sm font-medium whitespace-nowrap"
            >
              프로필
            </button>
            <button
              onClick={() => navigate('/settings')}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors text-sm font-medium whitespace-nowrap"
            >
              설정
            </button> */}
            <button
              onClick={logout}
              className="px-4 py-2 bg-red-50 border border-red-200 text-red-600 rounded-lg hover:bg-red-100 transition-colors text-sm font-medium whitespace-nowrap"
            >
              로그아웃
            </button>
        </div>
      </header>

      {/* 메인 컨텐츠 영역 */}
      <main className="w-full">
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 sm:p-6">
          
          {/* 검색 영역 */}
          <div className="mb-8">
            <div className="relative mb-6">
              <span className="absolute left-4 top-1/2 transform -translate-y-1/2 text-xl">
                🔍
              </span>
              <input
                type="text"
                placeholder="가족 구성원 이름, 연락처로 검색"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-12 pr-4 py-4 border-2 border-gray-300 rounded-lg text-lg focus:border-blue-500 focus:outline-none transition-colors"
              />
            </div>

            <div className="flex flex-col sm:flex-row gap-4 mb-8">
              <button 
                onClick={() => navigate('/invites/create')}
                className="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium text-base"
              >
                초대링크 생성
              </button>
              <button 
                onClick={() => navigate('/families')}
                className="flex-1 px-6 py-3 bg-white text-blue-600 border-2 border-blue-600 rounded-lg hover:bg-blue-50 transition-colors font-medium text-base"
              >
                가족 등록하기
              </button>
            </div>
          </div>

          {/* 데이터 영역 */}
          {!hasData ? (
            <div className="text-center py-16 px-4 sm:px-6 bg-gray-50 rounded-lg">
              <p className="text-lg sm:text-xl text-gray-600 leading-relaxed">
                아직 등록된 가족 정보가 없어요. 가족들에게 초대링크를 전달하거나 직접 가족 정보를 등록해보세요.
              </p>
            </div>
          ) : (
            <div className="w-full">
              {/* 테이블 헤더 - 데스크탑에서만 표시 */}
              <div className="hidden lg:block border-b-2 border-gray-200 pb-4 mb-4">
                <div className="grid grid-cols-12 gap-4 px-4 font-semibold text-gray-700">
                  <div className="col-span-3">이름</div>
                  <div className="col-span-3">생일</div>
                  <div className="col-span-3">연락처</div>
                  <div className="col-span-2">나와의 관계</div>
                  <div className="col-span-1"></div>
                </div>
              </div>

              {/* 구성원 목록 */}
              <div className="space-y-3">
                {filteredMembers.map((member: FamilyMemberWithRelationship) => (
                  <div 
                    key={member.memberId}
                    className="group bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
                  >
                    {/* 데스크탑 레이아웃 */}
                    <div className="hidden lg:grid grid-cols-12 gap-4 items-center">
                      <div className="col-span-3 font-medium text-gray-900">
                        {member.memberName}
                      </div>
                      <div className="col-span-3 text-gray-600">
                        {member.memberBirthday ? new Date(member.memberBirthday).toLocaleDateString('ko-KR', { 
                          year: 'numeric', 
                          month: 'long', 
                          day: 'numeric' 
                        }) : '-'}
                      </div>
                      <div className="col-span-3 text-gray-600">
                        {member.phoneNumberDisplay || '010-xxxx-xxxx'}
                      </div>
                      <div className="col-span-2 text-gray-600">
                        {member.relationshipDisplayName || '-'}
                      </div>
                      <div className="col-span-1">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate(`/families/${selectedFamilyId}/members/${member.memberId}`);
                          }}
                          className="px-3 py-1 bg-gray-100 hover:bg-gray-200 rounded-md text-sm font-medium transition-colors"
                        >
                          입력
                        </button>
                      </div>
                    </div>

                    {/* 모바일/태블릿 레이아웃 */}
                    <div className="lg:hidden">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <div className="font-medium text-gray-900 text-lg mb-1">
                            {member.memberName}
                          </div>
                          <div className="text-sm text-gray-500">
                            {member.relationshipDisplayName || '관계 미설정'}
                          </div>
                        </div>
                        <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs">
                          {member.relationshipDisplayName || '관계 설정'}
                        </span>
                      </div>
                      
                      <div className="space-y-1 text-sm text-gray-600 mb-3">
                        <div>
                          {member.memberBirthday ? new Date(member.memberBirthday).toLocaleDateString('ko-KR') : '생일 정보 없음'}
                        </div>
                        <div>
                          {member.phoneNumberDisplay || '010-xxxx-xxxx'}
                        </div>
                      </div>

                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/families/${selectedFamilyId}/members/${member.memberId}`);
                        }}
                        className="w-full py-2 bg-gray-100 hover:bg-gray-200 rounded-md text-sm font-medium transition-colors"
                      >
                        정보 입력
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </main>
    </>
  );
};

export default HomePage;