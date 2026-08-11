export interface Interview {
  id: string;
  company: string;
  role: string;
  startsAt: string;
  stage: string;
  prepared: boolean;
}

export interface DashboardStats {
  upcoming: number;
  thisWeek: number;
  prepared: number;
}

export const EMPTY_STATS: DashboardStats = { upcoming: 0, thisWeek: 0, prepared: 0 };
