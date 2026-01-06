/**
 * 음력/양력 변환 유틸리티 테스트
 * PRD-005: 멤버 목록 생일/나이 표시 기능
 */

import { formatDate, formatMonthDay, solarToLunar, lunarToSolar, formatBirthday, formatBirthdayShort } from '../lunar';

describe('lunar utilities', () => {
  describe('formatDate', () => {
    it('Date 객체를 YYYY.MM.DD 형식으로 포맷', () => {
      const date = new Date('1990-12-25');
      expect(formatDate(date)).toBe('1990.12.25');
    });

    it('ISO 문자열을 YYYY.MM.DD 형식으로 포맷', () => {
      expect(formatDate('2000-06-15')).toBe('2000.06.15');
    });

    it('한 자리 월/일을 0으로 패딩', () => {
      const date = new Date('2000-01-05');
      expect(formatDate(date)).toBe('2000.01.05');
    });
  });

  describe('formatMonthDay', () => {
    it('Date 객체를 MM.DD 형식으로 포맷', () => {
      const date = new Date('1990-12-25');
      expect(formatMonthDay(date)).toBe('12.25');
    });

    it('ISO 문자열을 MM.DD 형식으로 포맷', () => {
      expect(formatMonthDay('2000-06-15')).toBe('06.15');
    });
  });

  describe('solarToLunar', () => {
    it('양력 1990년 12월 25일을 음력으로 변환', () => {
      const date = new Date('1990-12-25');
      const lunar = solarToLunar(date);

      expect(lunar.year).toBe(1990);
      expect(lunar.month).toBe(11); // 음력 11월
      expect(lunar.day).toBe(9);    // 음력 9일
    });

    it('양력 2024년 2월 10일 (설날)을 음력으로 변환', () => {
      const date = new Date('2024-02-10');
      const lunar = solarToLunar(date);

      expect(lunar.year).toBe(2024);
      expect(lunar.month).toBe(1);  // 음력 1월
      expect(lunar.day).toBe(1);    // 음력 1일 (설날)
    });
  });

  describe('lunarToSolar', () => {
    it('음력 1990년 11월 9일을 양력으로 변환', () => {
      const solar = lunarToSolar(1990, 11, 9);

      expect(solar.getFullYear()).toBe(1990);
      expect(solar.getMonth()).toBe(11); // 12월 (0-indexed)
      expect(solar.getDate()).toBe(25);
    });

    it('음력 2024년 1월 1일 (설날)을 양력으로 변환', () => {
      const solar = lunarToSolar(2024, 1, 1);

      expect(solar.getFullYear()).toBe(2024);
      expect(solar.getMonth()).toBe(1);  // 2월 (0-indexed)
      expect(solar.getDate()).toBe(10);
    });
  });

  describe('formatBirthday', () => {
    describe('기본 표시 (showConverted = false)', () => {
      it('양력 생일은 날짜만 표시', () => {
        const result = formatBirthday('1990-12-25', 'SOLAR', false);
        expect(result).toBe('1990.12.25');
      });

      it('음력 생일은 (음) 라벨과 함께 표시', () => {
        const result = formatBirthday('1990-12-25', 'LUNAR', false);
        expect(result).toBe('(음) 1990.12.25');
      });

      it('birthdayType이 null이면 양력으로 표시', () => {
        const result = formatBirthday('1990-12-25', null, false);
        expect(result).toBe('1990.12.25');
      });
    });

    describe('변환 표시 (showConverted = true)', () => {
      it('양력 생일 토글 시 음력으로 변환 표시', () => {
        const result = formatBirthday('1990-12-25', 'SOLAR', true);
        // 1990.12.25 양력 -> 음력 1990.11.09
        expect(result).toBe('(음) 1990.11.09');
      });

      it('음력 생일 토글 시 양력으로 변환 표시', () => {
        // 음력 1990.12.25를 양력으로 변환
        const result = formatBirthday('1990-12-25', 'LUNAR', true);
        // 음력 1990.12.25 -> 양력 1991.02.09
        expect(result).toBe('1991.02.09');
      });
    });
  });

  describe('formatBirthdayShort', () => {
    it('양력 생일은 MM.DD 형식으로 표시', () => {
      const result = formatBirthdayShort('1990-12-25', 'SOLAR');
      expect(result).toBe('12.25');
    });

    it('음력 생일은 (음) MM.DD 형식으로 표시', () => {
      const result = formatBirthdayShort('1990-12-25', 'LUNAR');
      expect(result).toBe('(음) 12.25');
    });

    it('birthdayType이 null이면 양력으로 표시', () => {
      const result = formatBirthdayShort('1990-12-25', null);
      expect(result).toBe('12.25');
    });
  });
});
