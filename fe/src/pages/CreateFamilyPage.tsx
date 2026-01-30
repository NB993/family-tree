import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { FamilyService, SaveFamilyRequest } from '../api/services/familyService';
import { familyQueryKeys } from '../hooks/queries/useFamilyQueries';
import { ArrowLeft, Users } from 'lucide-react';

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
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: familyQueryKeys.list({ type: 'my' }),
      });
      alert('그룹이 생성되었습니다!');
      navigate('/home');
    },
    onError: (error: any) => {
      console.error('그룹 생성 실패:', error);
      alert('그룹 생성에 실패했습니다.');
    },
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

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

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  return (
    <div className="app-shell flex flex-col">
      {/* Header */}
      <header className="flex items-center gap-2 px-3 py-2 border-b border-border">
        <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
          <ArrowLeft className="h-3.5 w-3.5" strokeWidth={1.5} />
        </Button>
        <h1 className="text-sm font-medium">새 그룹 만들기</h1>
      </header>

      {/* Content */}
      <div className="flex-1 overflow-auto">
        <div className="p-3">
          <div className="flex flex-col items-center text-center py-3 mb-3">
            <Users className="w-8 h-8 text-primary mb-2" strokeWidth={1.5} />
            <p className="text-[10px] text-muted-foreground">
              새로운 그룹을 만들어 멤버들과 연결하세요
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-3">
            <div className="space-y-1">
              <Label htmlFor="name" className="text-xs">그룹명 *</Label>
              <Input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className={errors.name ? 'border-destructive' : ''}
                placeholder="그룹명 입력"
              />
              {errors.name && (
                <p className="text-[10px] text-destructive">{errors.name}</p>
              )}
            </div>

            <div className="space-y-1">
              <Label htmlFor="visibility" className="text-xs">공개 설정</Label>
              <Select
                value={formData.visibility}
                onValueChange={(value) => setFormData(prev => ({ ...prev, visibility: value as 'PUBLIC' | 'PRIVATE' }))}
              >
                <SelectTrigger className="h-8 text-xs">
                  <SelectValue placeholder="공개 설정 선택" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="PRIVATE" className="text-xs">비공개</SelectItem>
                  <SelectItem value="PUBLIC" className="text-xs">공개</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1">
              <Label htmlFor="description" className="text-xs">그룹 소개</Label>
              <Textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={2}
                placeholder="그룹 소개 (선택)"
                className="text-xs resize-none"
              />
            </div>

            <div className="space-y-1">
              <Label htmlFor="profileUrl" className="text-xs">프로필 이미지 URL</Label>
              <Input
                type="url"
                id="profileUrl"
                name="profileUrl"
                value={formData.profileUrl}
                onChange={handleChange}
                placeholder="https://... (선택)"
              />
            </div>
          </form>
        </div>
      </div>

      {/* Footer */}
      <div className="p-3 border-t border-border space-y-2">
        <Button
          onClick={handleSubmit}
          className="w-full"
          disabled={createFamilyMutation.isPending}
        >
          {createFamilyMutation.isPending ? '생성 중...' : '그룹 만들기'}
        </Button>
        <Button
          type="button"
          variant="outline"
          className="w-full"
          onClick={() => navigate('/home')}
        >
          취소
        </Button>
      </div>
    </div>
  );
};

export default CreateFamilyPage;
