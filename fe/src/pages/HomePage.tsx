import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useMyFamilies } from '../hooks/queries/useFamilyQueries';
import { AuthService } from '../api/services/authService';

const HomePage: React.FC = () => {
  const { data: familiesData, isLoading, isError } = useMyFamilies();
  const navigate = useNavigate();

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

  if (isLoading) {
    return (
      <div style={{ padding: '20px', textAlign: 'center' }}>
        <p>가족 정보를 불러오는 중...</p>
      </div>
    );
  }

  if (isError) {
    return (
      <div style={{ padding: '20px', textAlign: 'center' }}>
        <h2>오류가 발생했습니다</h2>
        <p>가족 정보를 불러올 수 없습니다.</p>
        <button onClick={() => window.location.reload()}>다시 시도</button>
      </div>
    );
  }

  const families = familiesData || [];

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      {/* 헤더 영역 */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: '30px',
        padding: '20px',
        backgroundColor: '#f8f9fa',
        borderRadius: '8px'
      }}>
        <div>
          <h1 style={{ fontSize: '28px', margin: '0 0 10px 0' }}>
            🏠 Family Tree
          </h1>
          <p style={{ margin: 0, color: '#666' }}>
            가족의 이야기를 따뜻하게 기록하세요
          </p>
        </div>
        
        <div style={{ textAlign: 'right' }}>
          {user && (
            <div style={{ marginBottom: '10px' }}>
              <p style={{ margin: 0, fontSize: '14px', color: '#666' }}>안녕하세요!</p>
              <p style={{ margin: 0, fontWeight: 'bold' }}>{user.name}님</p>
            </div>
          )}
          <button 
            onClick={handleLogout}
            style={{
              padding: '8px 16px',
              backgroundColor: 'white',
              border: '1px solid #ddd',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            로그아웃
          </button>
        </div>
      </div>

      {families.length === 0 ? (
        <div style={{
          textAlign: 'center',
          padding: '60px 20px',
          backgroundColor: 'white',
          border: '1px solid #e0e0e0',
          borderRadius: '8px'
        }}>
          <div style={{ fontSize: '60px', marginBottom: '20px' }}>👨‍👩‍👧‍👦</div>
          <h3 style={{ fontSize: '24px', marginBottom: '10px' }}>
            아직 가족이 없습니다
          </h3>
          <p style={{ color: '#666', marginBottom: '30px' }}>
            새로운 가족을 만들거나 초대 링크를 생성해보세요
          </p>
          <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
            <Link to="/families/create">
              <button style={{
                padding: '12px 24px',
                backgroundColor: '#007bff',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                fontSize: '16px',
                cursor: 'pointer'
              }}>
                새 가족 만들기
              </button>
            </Link>
            <Link to="/invites/create">
              <button style={{
                padding: '12px 24px',
                backgroundColor: '#28a745',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                fontSize: '16px',
                cursor: 'pointer'
              }}>
                초대 링크 생성
              </button>
            </Link>
            <Link to="/families/search">
              <button style={{
                padding: '12px 24px',
                backgroundColor: 'white',
                color: '#007bff',
                border: '1px solid #007bff',
                borderRadius: '4px',
                fontSize: '16px',
                cursor: 'pointer'
              }}>
                가족 찾기
              </button>
            </Link>
          </div>
        </div>
      ) : (
        <div>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            marginBottom: '20px'
          }}>
            <h2 style={{ margin: 0 }}>내 가족 ({families.length})</h2>
            <div style={{ display: 'flex', gap: '10px' }}>
              <Link to="/invites/create">
                <button style={{
                  padding: '8px 16px',
                  backgroundColor: '#28a745',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}>
                  초대하기
                </button>
              </Link>
              <Link to="/families/search">
                <button style={{
                  padding: '8px 16px',
                  backgroundColor: 'white',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}>
                  가족 찾기
                </button>
              </Link>
              <Link to="/families/create">
                <button style={{
                  padding: '8px 16px',
                  backgroundColor: 'white',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}>
                  가족 추가
                </button>
              </Link>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            {families.map((family: any) => (
              <Link 
                key={family.id} 
                to={`/families/${family.id}/members`}
                style={{ textDecoration: 'none', color: 'inherit' }}
              >
                <div style={{
                  padding: '20px',
                  backgroundColor: 'white',
                  border: '1px solid #e0e0e0',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  transition: 'box-shadow 0.2s'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <h3 style={{ margin: '0 0 5px 0' }}>{family.name}</h3>
                      <p style={{ margin: '0 0 5px 0', color: '#666', fontSize: '14px' }}>
                        구성원 {family.memberCount}명
                      </p>
                      {family.description && (
                        <p style={{ margin: 0, color: '#999', fontSize: '14px' }}>
                          {family.description}
                        </p>
                      )}
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <span style={{
                        padding: '4px 8px',
                        fontSize: '12px',
                        backgroundColor: family.isPublic ? '#d4edda' : '#f8f9fa',
                        color: family.isPublic ? '#155724' : '#6c757d',
                        borderRadius: '12px'
                      }}>
                        {family.isPublic ? '공개' : '비공개'}
                      </span>
                      <span style={{ color: '#999' }}>→</span>
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      )}

      {/* 추가 기능 섹션 */}
      <div style={{ marginTop: '50px', textAlign: 'center' }}>
        <h3 style={{ marginBottom: '20px' }}>다른 기능들</h3>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
          <div style={{
            padding: '30px',
            backgroundColor: 'white',
            border: '1px solid #e0e0e0',
            borderRadius: '8px',
            cursor: 'pointer'
          }}>
            <div style={{ fontSize: '36px', marginBottom: '10px' }}>🌳</div>
            <h4 style={{ margin: '0 0 5px 0' }}>가족 트리</h4>
            <p style={{ margin: 0, color: '#666', fontSize: '14px' }}>
              가족 관계도 보기
            </p>
          </div>
          <div style={{
            padding: '30px',
            backgroundColor: 'white',
            border: '1px solid #e0e0e0',
            borderRadius: '8px',
            cursor: 'pointer'
          }}>
            <div style={{ fontSize: '36px', marginBottom: '10px' }}>📱</div>
            <h4 style={{ margin: '0 0 5px 0' }}>연락처</h4>
            <p style={{ margin: 0, color: '#666', fontSize: '14px' }}>
              가족 연락처 관리
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;