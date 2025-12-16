export function StatCard({
  title,
  value,
  icon,
  change,
  color,
}: {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  change: string;
  color: string;
}) {
  return (
    <article className="flex items-center gap-4 rounded-lg border border-gray-100 bg-white p-6 w-full overflow-hidden">
      <span className="rounded-full bg-blue-100 p-3 text-blue-600 shrink-0">
        {icon}
      </span>
      <div className="min-w-0">
        <p className="text-2xl font-semibold text-gray-900 lg:truncate">{value}</p>
        <p className="text-sm text-gray-500 lg:truncate">{title}</p>
        <p className={`text-xs mt-1 font-medium lg:truncate ${color}`}>{change}</p>
      </div>
    </article>
  );
}
