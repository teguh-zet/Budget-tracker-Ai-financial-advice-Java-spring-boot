export default function formatRupiah (value: string): string {
    const number = parseInt(value.replace(/\D/g, ""));
    return isNaN(number) ? "" :
    new Intl.NumberFormat("id-ID", {
        style: "currency",
        currency: "IDR",
        minimumFractionDigits: 0,
    }).format(number);
}