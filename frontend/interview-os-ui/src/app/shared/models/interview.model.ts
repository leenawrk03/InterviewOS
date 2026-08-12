export interface Interview {
  id: string;
  title: string | null;
  description: string | null;
  startTime: string | null;
  endTime: string | null;
  link: string | null;
}

export interface DashboardStats {
  upcoming: number;
  thisWeek: number;
  prepared: number;
}

export const EMPTY_STATS: DashboardStats = {
  upcoming: 0,
  thisWeek: 0,
  prepared: 0
};