export default function formatRupiah(harga: number | string | undefined) {
    if (harga === undefined || harga === null) {
        return "Rp. 0";
    }
    const numValue = typeof harga === "string" ? parseInt(harga.replace(/\D/g, "")) : harga;
    if (isNaN(numValue)) {
        return "Rp. 0";
    }
    return `Rp. ${numValue.toLocaleString("id-ID")}`;
}