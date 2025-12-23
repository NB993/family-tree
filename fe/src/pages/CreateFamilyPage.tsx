import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card, CardContent, CardHeader } from '../components/common/Card';
import { Button } from '../components/common/Button';
import { FamilyService, SaveFamilyRequest } from '../api/services/familyService';
import { familyQueryKeys } from '../hooks/queries/useFamilyQueries';

const CreateFamilyPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const familyService = FamilyService.getInstance();

  const [formData, setFormData] = useState<SaveFamilyRequest>({
    name: '',
    visibility: 'PRIVATE',
    description: '',
    profileUrl: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const createFamilyMutation = useMutation({
    mutationFn: (request: SaveFamilyRequest) => familyService.createFamily(request),
    onSuccess: (data) => {
      // 내 가족 목록 무효화
      queryClient.invalidateQueries({
        queryKey: familyQueryKeys.list({ type: 'my' }),
      });
      
      // 성공 메시지와 함께 홈으로 이동
      alert('그룹이 성공적으로 생성되었습니다!');
      navigate('/home');
    },
    onError: (error: any) => {
      console.error('그룹 생성 실패:', error);
      alert('그룹 생성에 실패했습니다. 다시 시도해주세요.');
    },
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // 간단한 유효성 검사
    const newErrors: Record<string, string> = {};
    
    if (!formData.name.trim()) {
      newErrors.name = '그룹명을 입력해주세요.';
    } else if (formData.name.length < 2) {
      newErrors.name = '그룹명은 2자 이상 입력해주세요.';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setErrors({});
    createFamilyMutation.mutate(formData);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    
    // 입력 시 해당 필드 에러 제거
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  return (
    <div className="container">
      <div className="py-8">
        <div className="max-w-md mx-auto">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold text-gray-900">
                  새 그룹 만들기
                </h1>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => navigate('/home')}
                >
                  취소
                </Button>
              </div>
              <p className="text-gray-600 mt-2">
                새로운 그룹을 만들어 멤버들과 연결하세요
              </p>
            </CardHeader>
            
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* 그룹명 */}
                <div>
                  <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                    그룹명 *
                  </label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500 ${
                      errors.name ? 'border-red-500' : 'border-gray-300'
                    }`}
                    placeholder="그룹명 입력"
                  />
                  {errors.name && (
                    <p className="mt-1 text-sm text-red-600">{errors.name}</p>
                  )}
                </div>

                {/* 공개 설정 */}
                <div>
                  <label htmlFor="visibility" className="block text-sm font-medium text-gray-700 mb-2">
                    공개 설정
                  </label>
                  <select
                    id="visibility"
                    name="visibility"
                    value={formData.visibility}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500"
                  >
                    <option value="PRIVATE">비공개 - 초대받은 사람만 참여</option>
                    <option value="PUBLIC">공개 - 누구나 참여 요청 가능</option>
                  </select>
                </div>

                {/* 그룹 소개 */}
                <div>
                  <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                    그룹 소개
                  </label>
                  <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    rows={3}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500"
                    placeholder="그룹을 소개해주세요 (선택사항)"
                  />
                </div>

                {/* 프로필 이미지 URL */}
                <div>
                  <label htmlFor="profileUrl" className="block text-sm font-medium text-gray-700 mb-2">
                    프로필 이미지 URL
                  </label>
                  <input
                    type="url"
                    id="profileUrl"
                    name="profileUrl"
                    value={formData.profileUrl}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-orange-500"
                    placeholder="https://example.com/family-photo.jpg (선택사항)"
                  />
                </div>

                {/* 버튼 */}
                <div className="flex gap-3">
                  <Button
                    type="button"
                    variant="outline"
                    fullWidth
                    onClick={() => navigate('/home')}
                  >
                    취소
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    fullWidth
                    loading={createFamilyMutation.isPending}
                  >
                    그룹 만들기
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default CreateFamilyPage;