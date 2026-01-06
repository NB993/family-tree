/**
 * 나이 계산 유틸리티 테스트
 * PRD-005: 멤버 목록 생일/나이 표시 기능
 */

import { getKoreanAge, getWesternAge, formatAge } from '../age';

describe('age utilities', () => {
  beforeAll(() => {
    // 2026년 1월 6일로 고정
    jest.useFakeTimers();
    jest.setSystemTime(new Date('2026-01-06T12:00:00Z'));
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  describe('getKoreanAge', () => {
    it('1990년생은 2026년에 37살 (한국나이)', () => {
      const birthday = '1990-12-25';
      expect(getKoreanAge(birthday)).toBe(37);
    });

    it('2000년생은 2026년에 27살 (한국나이)', () => {
      const birthday = '2000-06-15';
      expect(getKoreanAge(birthday)).toBe(27);
    });

    it('2026년생은 2026년에 1살 (한국나이)', () => {
      const birthday = '2026-01-01';
      expect(getKoreanAge(birthday)).toBe(1);
    });

    it('Date 객체도 처리 가능', () => {
      const birthday = new Date('1990-12-25');
      expect(getKoreanAge(birthday)).toBe(37);
    });
  });

  describe('getWesternAge', () => {
    it('1990년 12월 25일생은 2026년 1월 6일에 만 35세', () => {
      // 생일이 아직 안 지났으므로 35세
      const birthday = '1990-12-25';
      expect(getWesternAge(birthday)).toBe(35);
    });

    it('1990년 1월 1일생은 2026년 1월 6일에 만 36세', () => {
      // 생일이 지났으므로 36세
      const birthday = '1990-01-01';
      expect(getWesternAge(birthday)).toBe(36);
    });

    it('2000년 1월 6일생은 2026년 1월 6일에 만 26세', () => {
      // 생일 당일
      const birthday = '2000-01-06';
      expect(getWesternAge(birthday)).toBe(26);
    });

    it('2000년 1월 7일생은 2026년 1월 6일에 만 25세', () => {
      // 생일 하루 전
      const birthday = '2000-01-07';
      expect(getWesternAge(birthday)).toBe(25);
    });

    it('Date 객체도 처리 가능', () => {
      const birthday = new Date('1990-01-01');
      expect(getWesternAge(birthday)).toBe(36);
    });
  });

  describe('formatAge', () => {
    it('한국나이 포맷: (37)', () => {
      const birthday = '1990-12-25';
      expect(formatAge(birthday, 'korean')).toBe('(37)');
    });

    it('만나이 포맷: (만 35)', () => {
      const birthday = '1990-12-25';
      expect(formatAge(birthday, 'western')).toBe('(만 35)');
    });
  });
});
