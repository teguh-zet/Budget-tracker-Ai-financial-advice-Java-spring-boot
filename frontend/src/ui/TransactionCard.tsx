export default function TransactionCard({
    title, 
    value
}: {
    title: string;
    value: string | number;
}) {
    return (
        <div className="bg-indigo-50 p-4 rounded-xl shadow">
            <p className="text-sm text-gray-500">{title}</p>
            <p className="text-2xl font-bold text-indigo-600">
                {value}
            </p>
        </div>
    )
}