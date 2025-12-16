export interface SummaryItem {
  user_id: number;
  created_at: string;
  ai_summary: string;
  ai_recomendation: string;
  ai_trend_analysis?: string;
}