/**
 * korean-lunar-calendar 라이브러리 타입 정의
 * @see https://github.com/usingsky/korean_lunar_calendar_js
 */

declare module 'korean-lunar-calendar' {
  interface LunarCalendar {
    year: number;
    month: number;
    day: number;
    intercalation: boolean;  // 윤달 여부
  }

  interface SolarCalendar {
    year: number;
    month: number;
    day: number;
  }

  class KoreanLunarCalendar {
    constructor();

    /**
     * 양력 날짜 설정
     * @param year 연도
     * @param month 월 (1-12)
     * @param day 일
     */
    setSolarDate(year: number, month: number, day: number): boolean;

    /**
     * 음력 날짜 설정
     * @param year 연도
     * @param month 월 (1-12)
     * @param day 일
     * @param intercalation 윤달 여부
     */
    setLunarDate(year: number, month: number, day: number, intercalation?: boolean): boolean;

    /**
     * 음력 달력 정보 반환
     */
    getLunarCalendar(): LunarCalendar;

    /**
     * 양력 달력 정보 반환
     */
    getSolarCalendar(): SolarCalendar;

    /**
     * 간지 반환 (연월일)
     */
    getGapja(): string;

    /**
     * 한국식 간지 반환
     */
    getKoreanGapja(): string;

    /**
     * 중국식 간지 반환
     */
    getChineseGapja(): string;
  }

  export = KoreanLunarCalendar;
}
