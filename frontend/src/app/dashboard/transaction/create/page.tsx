"use client"

import TransactionForm from "@/pages/TransactionForm";
import { useRouter } from "next/navigation";
import { useState } from "react";
import Modal from "@/ui/Modal";
import LoadingSpinnerScreen from "@/ui/LoadingSpinnerScreen";
import { ModalProps } from "@/interfaces/IModal";
import { TransactionFormData } from "@/interfaces/ITransaction";
import { createTransaction } from "@/services/transaction";

export default function CreateTransactionPage() {
    const router = useRouter();
    const [isSubmitting, setISubmitting] = useState(false);
    const [modal, setModal] = useState<ModalProps | null>(null);

    const handleSubmit = async (form: TransactionFormData) => {
        setISubmitting(true);
        try {
            await createTransaction({
                ...form,
                category_id: form.categoryId    
            });
            setModal({
                type: "success",
                message: "Transaksi Berhasil Ditambahkan"
            });
        } catch (error) {
            if(error instanceof Error){
                setModal({ message: error.message, type: "danger"});
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger"});
            }
        } finally {
            setISubmitting(false);
        }
    }
    
    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Buat Transaksi Baru</h1>
            {isSubmitting && <LoadingSpinnerScreen />}
            {modal && (
                <Modal 
                    type={modal.type}
                    message={modal.message}
                    onOk={() => {
                        setModal(null);
                        if(modal.type === "success"){
                            router.push('/dashboard/transaction')
                        }
                    }}
                />
            )}
            <TransactionForm onSubmit={handleSubmit}/>
        </div>
    )
}