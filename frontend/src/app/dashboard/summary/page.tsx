"use client"

import React, {useState, useEffect} from "react";
import {FaRobot, FaSync, FaFilePdf, FaDownload} from "react-icons/fa";
import {
    generateMonthlySummary,
    fetchAllMonthlySummaries,
    downloadSummaryPDF
} from "@/services/monthlySummary";
import {profile as fetchProfile} from "@/services/auth";
import Modal from "@/ui/Modal";
import { ModalProps } from "@/interfaces/IModal";
import { SummaryItem } from "@/interfaces/ISummary";
import { LLMResponse } from "@/interfaces/ILLM";
import { error } from "console";

export default function SummaryPage() {
    const [loading, setLoading] = useState<boolean>(false);
    const [response, setResponse] = useState<LLMResponse | null>(null);
    const [alreadyGenerated, setAlreadyGenerated] = useState<boolean>(false);
    const [errorModal, setErrorModal] = useState<ModalProps | null>(null);
    const [summaryId, setSummaryId] = useState<number | null>(null);
    const [downloading, setDownloading] = useState<boolean>(false);

    const checkTodySummaries = async() => {
        try {
            const token = localStorage.getItem("token");
            const profileRes = await fetchProfile(token || "");
            const userId = profileRes.data.id;

            const res = await fetchAllMonthlySummaries();
            const today = new Date().toISOString().slice(0, 10);

            const found = res.data.find(
                (item: SummaryItem) => 
                    item.user_id === userId &&
                item.created_at?.slice(0, 10) === today
            );

            if (found) {
                setAlreadyGenerated(true);
                setSummaryId(found.id);
                setResponse({
                    summary: found.ai_summary || "",
                    recommendations: found.ai_recomendation
                        ? found.ai_recomendation.split("\n").filter((line: string) => line.trim().length > 0)
                        : [],
                    trend_analysis: found.ai_trend_analysis || ""
                })
            }
        } catch (error) {
            if(error instanceof Error) {
                setErrorModal({message: error.message, type: "danger"})
            } else {
                setErrorModal({message: "Terjadi Kesalahan", type: "danger"})
            }
        }
    }

    useEffect(() => {
        checkTodySummaries();
    }, []);

    const handleGenerate = async () => {
        setLoading(true);
        try {
            const result = await generateMonthlySummary();

            if(result.success && result.data) {
                setResponse({
                    summary: result.data.summary,
                    recommendations: result.data.recommendations,
                    trend_analysis: result.data.trend_analysis
                });
                setAlreadyGenerated(true);
                // Get summary ID from the latest summary
                const summariesRes = await fetchAllMonthlySummaries();
                if (summariesRes.data && summariesRes.data.length > 0) {
                    const latest = summariesRes.data[0];
                    setSummaryId(latest.id);
                }
            } else {
                throw new Error(result.message || "Gagal menghasilkan Ringkasan")
            }
        } catch (error) {
            if(error instanceof Error) {
                setErrorModal({message: error.message, type: "danger"})
            } else {
                setErrorModal({message: "Terjadi Kesalahan", type: "danger"})
            }
        } finally {
            setLoading(false)
        }
    }

    const handleDownloadPDF = async () => {
        if (!summaryId) {
            setErrorModal({message: "Summary ID tidak ditemukan", type: "danger"});
            return;
        }
        
        setDownloading(true);
        try {
            await downloadSummaryPDF(summaryId);
        } catch (error) {
            if(error instanceof Error) {
                setErrorModal({message: error.message, type: "danger"})
            } else {
                setErrorModal({message: "Gagal mengunduh PDF", type: "danger"})
            }
        } finally {
            setDownloading(false);
        }
    }

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">AI Financial Summary</h2>
                <div className="flex gap-2">
                    {summaryId && alreadyGenerated && (
                        <button
                            onClick={handleDownloadPDF}
                            disabled={downloading}
                            className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md flex items-center gap-2"
                        >
                            <span className="inline-block">
                                {downloading ? <FaSync className="animate-spin" /> : <FaFilePdf />}
                            </span>
                            {downloading ? "Mengunduh..." : "Download PDF"}
                        </button>
                    )}
                    <button
                        onClick={handleGenerate}
                        disabled={loading}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md flex items-center gap-2"
                    >
                        <span className="inline-block">
                            {loading ? <FaSync className="animate-spin" /> : <FaRobot />}
                        </span>
                        {loading ? "Menganalisis..." : "Generate Summary"}
                    </button>
                </div>
            </div>
            {response && (                
                <div className="space-y-4">
                    <div className="bg-white rounded-xl p-4 shadow">
                        <h3 className="text-lg font-semibold mb-2 text-indigo-600">📄 Ringkasan</h3>
                        <p className="text-gray-700">{response.summary}</p>
                    </div>

                    {Array.isArray(response.recommendations) && (
                        <div className="bg-white rounded-xl p-4 shadow">
                            <h3 className="text-lg font-semibold mb-2 text-green-600">💡 Rekomendasi</h3>
                            <ul className="list-disc list-inside space-y-1 text-gray-700">
                                {response.recommendations.map((rec, idx) => (
                                    <li key={idx}>{rec}</li>
                                ))}
                            </ul>
                        </div>
                    )}

                    <div className="bg-white rounded-xl p-4 shadow">
                        <h3 className="text-lg font-semibold mb-2 text-yellow-600">📈 Analisis Tren</h3>
                        {response.trend_analysis && response.trend_analysis.trim().length > 0 ? (
                            <p className="text-gray-700">{response.trend_analysis}</p>
                        ) : (
                            <p className="text-gray-400 italic">
                                Analisis tren tidak tersedia. Silakan generate summary baru untuk mendapatkan analisis tren.
                            </p>
                        )}
                    </div>

                    <p className="text-sm text-gray-500 mt-4">
                        Kamu bisa meminta ringkasan baru di esok hari.
                    </p>
                    <p className="text-xs text-gray-400">Powered by TNG: Grok 4 Fast</p>
                </div>
            )}

            {!response && !loading && !alreadyGenerated && (
                <div className="text-gray-500 text-sm">
                    Klik Tombol <b>&quot;Generate Summary&quot;</b> untuk mendapatkan analisis otomatis
                </div>
            )}

            {errorModal && (
                <Modal 
                    type="danger"
                    message={errorModal.message}
                    onOk={() => setErrorModal(null)}
                />
            )}
        </div>
    )
}