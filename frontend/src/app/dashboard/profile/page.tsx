"use client"

import React, { useEffect, useState, useRef } from "react";
import { profile as fetchProfile } from "@/services/auth";
import { updateUser, uploadProfilePicture, getProfilePictureUrl, getProfilePictureBlobUrl, deleteProfilePicture } from "@/services/user";
import LoadingSpinnerScreen from "@/ui/LoadingSpinnerScreen";
import Modal from "@/ui/Modal";
import { ModalProps } from "@/interfaces/IModal";
import { FaCamera, FaTrash } from "react-icons/fa";

export default function Profilepage() {
    const [form, setForm] = useState({
        id: 0,
        name: "",
        email: "",
        number: "",
        profilePicture: ""
    });

    const [loading, setLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [modal, setModal] = useState<ModalProps | null> (null);
    const [profilePictureBlobUrl, setProfilePictureBlobUrl] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const loadProfile = async () => {
        try {
            const token = localStorage.getItem("token");
            if(!token) return;
            const res = await fetchProfile(token);

            const rawNumber = res.data.number || "";
            const cleanNumber = rawNumber.startsWith("+62") ? rawNumber.replace("+62", "") : rawNumber;

            setForm({
                ...res.data,
                number: cleanNumber,
                profilePicture: res.data.profilePicture || ""
            });
            
            // Load profile picture as blob URL if exists
            if (res.data.profilePicture) {
                const blobUrl = await getProfilePictureBlobUrl(res.data.profilePicture, res.data.id);
                setProfilePictureBlobUrl(blobUrl);
            } else {
                setProfilePictureBlobUrl(null);
            }
        } catch (error) {
            if(error instanceof Error) {
                setModal({ message: error.message, type: "danger"})
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger"})
            }
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadProfile();
    }, []);
    
    // Cleanup blob URL on unmount or when it changes
    useEffect(() => {
        return () => {
            if (profilePictureBlobUrl) {
                URL.revokeObjectURL(profilePictureBlobUrl);
            }
        };
    }, [profilePictureBlobUrl]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        const sanitizedValue = name === "number" ? value.replace(/[^0-9]/g, "") : value;
        setForm((prev) => ({...prev, [name]: sanitizedValue}));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const updated = await updateUser(form.id, {
                name: form.name,
                email: form.email,
                number: `+62${form.number}`
            });
            setForm({
                ...updated.data,
                number: updated.data.number.replace("+62", ""),
            });
            setModal({ message: "Profil Berhasil Diperbarui", type: "success"})
        } catch (error) {
            if(error instanceof Error) {
                setModal({ message: error.message, type: "danger"})
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger"})
            }
        }
    }

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            setModal({ message: "File harus berupa gambar", type: "danger" });
            return;
        }

        // Validate file size (5MB)
        if (file.size > 5 * 1024 * 1024) {
            setModal({ message: "Ukuran file tidak boleh lebih dari 5MB", type: "danger" });
            return;
        }

        setUploading(true);
        try {
            const result = await uploadProfilePicture(file);
            // Update form with new profile picture URL
            if (result && result.data && result.data.profilePicture) {
                setForm(prev => ({ ...prev, profilePicture: result.data.profilePicture }));
                // Load new profile picture as blob URL
                const blobUrl = await getProfilePictureBlobUrl(result.data.profilePicture, form.id);
                setProfilePictureBlobUrl(blobUrl);
            } else {
                // Reload profile to get updated data
                await loadProfile();
            }
            setModal({ message: "Foto profil berhasil diupload", type: "success" });
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Gagal upload foto profil", type: "danger" });
            }
        } finally {
            setUploading(false);
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
        }
    };

    const handleDeletePicture = async () => {
        if (!form.profilePicture) return;
        
        setUploading(true);
        try {
            await deleteProfilePicture();
            setForm(prev => ({ ...prev, profilePicture: "" }));
            // Clean up blob URL
            if (profilePictureBlobUrl) {
                URL.revokeObjectURL(profilePictureBlobUrl);
                setProfilePictureBlobUrl(null);
            }
            setModal({ message: "Foto profil berhasil dihapus", type: "success" });
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Gagal menghapus foto profil", type: "danger" });
            }
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="max-w-xl mx-auto p-6 space-y-6">
            <h2 className="text-2xl font-bold mb-4">Profil Pengguna</h2>
            
            {/* Profile Picture Section */}
            <div className="flex flex-col items-center mb-6">
                <div className="relative">
                    <div className="w-32 h-32 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden border-4 border-indigo-600">
                        {profilePictureBlobUrl ? (
                            <img 
                                src={profilePictureBlobUrl} 
                                alt="Profile" 
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                    // Hide image on error, will show initial instead
                                    const target = e.target as HTMLImageElement;
                                    target.style.display = 'none';
                                    setProfilePictureBlobUrl(null);
                                }}
                                key={`profile-${form.profilePicture}-${form.id}`}
                            />
                        ) : (
                            <span className="text-4xl text-gray-500 font-bold">
                                {form.name ? form.name[0].toUpperCase() : "U"}
                            </span>
                        )}
                    </div>
                    <button
                        type="button"
                        onClick={() => fileInputRef.current?.click()}
                        disabled={uploading}
                        className="absolute bottom-0 right-0 bg-indigo-600 text-white p-2 rounded-full hover:bg-indigo-700 disabled:opacity-50"
                    >
                        <FaCamera />
                    </button>
                    {form.profilePicture && (
                        <button
                            type="button"
                            onClick={handleDeletePicture}
                            disabled={uploading}
                            className="absolute top-0 right-0 bg-red-600 text-white p-2 rounded-full hover:bg-red-700 disabled:opacity-50"
                        >
                            <FaTrash className="text-xs" />
                        </button>
                    )}
                </div>
                <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    onChange={handleFileChange}
                    className="hidden"
                />
                {uploading && (
                    <p className="text-sm text-gray-500 mt-2">Mengupload foto...</p>
                )}
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label htmlFor="name" className="block mb-1 text-sm font-medium text-gray-700">Nama</label>
                    <input 
                        type="text" 
                        value={form.name}
                        onChange={handleChange}
                        name="name"
                        className="w-full border rounded px-4 py-2"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="email" className="block mb-1 text-sm font-medium text-gray-700">Email</label>
                    <input 
                        type="email" 
                        value={form.email}
                        onChange={handleChange}
                        name="email"
                        className="w-full border rounded px-4 py-2"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="number" className="block mb-1 text-sm font-medium text-gray-700">Nomor Telepon</label>
                    <div className="relative">
                        <div className="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500 text-sm">+62</div>
                        <input 
                            type="text" 
                            value={form.number}
                            onChange={handleChange}
                            name="number"
                            className="w-full border rounded px-4 py-2 pl-12"
                            required
                        />
                    </div>
                </div>

                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded disabled:opacity-50"
                >
                    {isSubmitting ? "Menyimpan..." : "Simpan Perubahan"}
                </button>
            </form>

            {modal && (
                <Modal 
                    type={modal.type}
                    message={modal.message}
                    onOk={() => setModal(null)}
                />
            )}
        </div>
    )   
}