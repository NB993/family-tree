import React, { useState, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useMyFamilies, useFamilyMembers } from '../hooks/queries/useFamilyQueries';
import { AuthService } from '../api/services/authService';
import { FamilyMemberWithRelationship } from '../api/services/familyService';

const HomePage: React.FC = () => {
  const { data: familiesData, isLoading, isError } = useMyFamilies();
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFamilyId, setSelectedFamilyId] = useState<number | null>(null);

  // 선택된 가족의 구성원 데이터 가져오기
  const { data: membersData } = useFamilyMembers(selectedFamilyId || 0);

  // 첫 번째 가족을 기본 선택
  React.useEffect(() => {
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

  const handleLogout = async () => {
    try {
      const authService = AuthService.getInstance();
      await authService.logout();
      authService.clearAllAuthData();
      navigate('/login');
    } catch (error) {
      console.error('로그아웃 중 오류 발생:', error);
      AuthService.getInstance().clearAllAuthData();
      navigate('/login');
    }
  };

  const userInfo = localStorage.getItem('userInfo');
  const user = userInfo ? JSON.parse(userInfo) : null;

  const families = familiesData || [];
  const hasData = families.length > 0 && membersData && membersData.length > 0;

  return (
    <div style={{ 
      minHeight: '100vh',
      backgroundColor: '#f8f9fa',
      padding: '20px'
    }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        {/* 헤더 영역 */}
        <div style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          marginBottom: '20px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
            <h1 style={{ fontSize: '24px', margin: 0 }}>
              🏠 Family Tree
            </h1>
            <span style={{ color: '#666', fontSize: '14px' }}>
              {user ? `${user.name}님` : '가족 트리'}
            </span>
          </div>
          
          <div style={{ display: 'flex', gap: '10px' }}>
            <button 
              onClick={() => navigate('/profile')}
              style={{
                padding: '8px 16px',
                backgroundColor: 'white',
                border: '1px solid #ddd',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '14px'
              }}
            >
              프로필
            </button>
            <button 
              onClick={() => navigate('/settings')}
              style={{
                padding: '8px 16px',
                backgroundColor: 'white',
                border: '1px solid #ddd',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '14px'
              }}
            >
              버튼 영역
            </button>
          </div>
        </div>

        {/* 검색 영역 */}
        <div style={{
          backgroundColor: 'white',
          border: '1px solid #e0e0e0',
          borderRadius: '8px',
          padding: '30px',
          marginBottom: '20px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
            <div style={{ position: 'relative', flex: 1 }}>
              <span style={{
                position: 'absolute',
                left: '12px',
                top: '50%',
                transform: 'translateY(-50%)',
                fontSize: '18px'
              }}>🔍</span>
              <input
                type="text"
                placeholder="FamilyMember 검색 영역"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 12px 12px 40px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '16px',
                  outline: 'none'
                }}
                onFocus={(e) => e.target.style.borderColor = '#007bff'}
                onBlur={(e) => e.target.style.borderColor = '#ddd'}
              />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
            <button 
              onClick={() => navigate('/invites/create')}
              style={{
                padding: '10px 20px',
                backgroundColor: 'white',
                border: '1px solid #ddd',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '14px'
              }}
            >
              초대링크 생성
            </button>
            <button 
              onClick={() => navigate('/families')}
              style={{
                padding: '10px 20px',
                backgroundColor: 'white',
                border: '1px solid #ddd',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '14px'
              }}
            >
              등록하기
            </button>
          </div>

          {!hasData ? (
            <div style={{
              textAlign: 'center',
              padding: '60px 20px',
              backgroundColor: '#f8f9fa',
              borderRadius: '8px'
            }}>
              <p style={{ color: '#666', fontSize: '16px', lineHeight: '1.6' }}>
                아직 등록된 가족 정보가 없어요.<br/>
                가족들에게 초대링크를 전달하거나<br/>
                직접 가족 정보를 등록해보세요.
              </p>
            </div>
          ) : (
            <div>
              {/* 구성원 목록 헤더 */}
              <div style={{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center',
                marginBottom: '15px'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                  <span style={{ fontSize: '14px', color: '#666' }}>이름</span>
                  <span style={{ fontSize: '14px', color: '#666' }}>생일</span>
                  <span style={{ fontSize: '14px', color: '#666' }}>연락처</span>
                  <span style={{ fontSize: '14px', color: '#666' }}>나와의 관계</span>
                </div>
              </div>

              {/* 구성원 목록 */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {filteredMembers.map((member: FamilyMemberWithRelationship) => (
                  <div 
                    key={member.memberId}
                    style={{
                      padding: '15px 20px',
                      backgroundColor: 'white',
                      border: '1px solid #e0e0e0',
                      borderRadius: '4px',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      cursor: 'pointer',
                      transition: 'box-shadow 0.2s'
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)'}
                    onMouseLeave={(e) => e.currentTarget.style.boxShadow = 'none'}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '30px', flex: 1 }}>
                      <span style={{ fontWeight: 'bold', minWidth: '80px' }}>{member.memberName}</span>
                      <span style={{ color: '#666', fontSize: '14px', minWidth: '100px' }}>
                        {member.memberBirthday ? new Date(member.memberBirthday).toLocaleDateString('ko-KR', { 
                          year: 'numeric', 
                          month: 'long', 
                          day: 'numeric' 
                        }) : '-'}
                      </span>
                      <span style={{ color: '#666', fontSize: '14px', minWidth: '120px' }}>
                        {member.phoneNumberDisplay || '010-xxxx-xxxx'}
                      </span>
                      <span style={{ 
                        color: '#666', 
                        fontSize: '14px',
                        minWidth: '80px'
                      }}>
                        {member.relationshipDisplayName || '-'}
                      </span>
                    </div>
                    <button
                      style={{
                        padding: '6px 12px',
                        backgroundColor: 'white',
                        border: '1px solid #ddd',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        fontSize: '14px'
                      }}
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/families/${selectedFamilyId}/members/${member.memberId}`);
                      }}
                    >
                      입력
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* 하단 기능 카드들 */}
        <div style={{ marginTop: '30px' }}>
          <h3 style={{ fontSize: '18px', marginBottom: '15px' }}>다른 기능들</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
            <div 
              style={{
                padding: '25px',
                backgroundColor: 'white',
                border: '1px solid #e0e0e0',
                borderRadius: '8px',
                cursor: 'pointer',
                textAlign: 'center',
                transition: 'box-shadow 0.2s'
              }}
              onMouseEnter={(e) => e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)'}
              onMouseLeave={(e) => e.currentTarget.style.boxShadow = 'none'}
              onClick={() => navigate('/tree')}
            >
              <div style={{ fontSize: '32px', marginBottom: '10px' }}>🌳</div>
              <h4 style={{ margin: '0 0 5px 0', fontSize: '16px' }}>가족 트리</h4>
              <p style={{ margin: 0, color: '#666', fontSize: '13px' }}>
                가족 관계도 보기
              </p>
            </div>
            <div 
              style={{
                padding: '25px',
                backgroundColor: 'white',
                border: '1px solid #e0e0e0',
                borderRadius: '8px',
                cursor: 'pointer',
                textAlign: 'center',
                transition: 'box-shadow 0.2s'
              }}
              onMouseEnter={(e) => e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)'}
              onMouseLeave={(e) => e.currentTarget.style.boxShadow = 'none'}
              onClick={() => navigate('/contacts')}
            >
              <div style={{ fontSize: '32px', marginBottom: '10px' }}>📱</div>
              <h4 style={{ margin: '0 0 5px 0', fontSize: '16px' }}>연락처</h4>
              <p style={{ margin: 0, color: '#666', fontSize: '13px' }}>
                가족 연락처 관리
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;