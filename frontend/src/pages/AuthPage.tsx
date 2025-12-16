"use client"
import react, { useRef, useState } from "react";
import Image from "next/image";
import { AiOutlineEyeInvisible, AiOutlineEye } from "react-icons/ai";
import { useRouter } from "next/navigation";
import { login, register } from "@/services/auth";
import LoadingSpinnerButton from "@/ui/LoadingSpinnerButton";
import Modal from "@/ui/Modal";

const AuthPage = () => {
    const [type, setType] = useState<"login" | "register">("login");
    const [showPassword, setShowPassword] = useState(false);
    const termsCheckBoxRef = useRef<HTMLInputElement>(null);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [loading, setLoading] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const router = useRouter();

    const [formData, setFormData] = useState({
        name: "",
        number: "",
        email: "",
        password: "",
    })

    const isLogin = type === "login";

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrors({});
        setLoading(true);

        console.log(type)
        
        try {
            let response;

            if(isLogin) {
                response = await login({
                    email: formData.email,
                    password: formData.password
                });
            } else {
                if(!termsCheckBoxRef.current?.checked) {
                    setErrors({ terms: "Kamu Harus Mensetujui Terms & Privacy "});
                    return
                }

                response = await register({
                    ...formData,
                    number: `+62${formData.number}`
                });
            }

            
            const token = response.data.token;
            localStorage.setItem("token", token);
            router.push("/dashboard");
        } catch (error) {
            if(error instanceof Error) {
                setErrors({ general: error.message})
            }
        } finally {
            setLoading(false)
        }
    }
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-4 py-12">
      <div
        className="grid grid-cols-1 
            md:grid-cols-2 w-full max-w-7xl 
            bg-white shadow-2xl rounded-2xl overflow-hidden"
      >
        {/* bagian kiri card */}
        <div className="p-8 sm:p-12 min-h-[750px] flex flex-col justify-center">
            {/* Ini bagian title dan sub title auth */}
          <h2 className="text-3xl font-bold text-gray-900 mb-2">
            {isLogin ? "Sign In" : "Sign Up"}
          </h2>
          <p className="text-sm text-gray-600 mb-4">
            {isLogin ? "Wellcome Back!" : "Let's Sign Up To Get Started.!"}
          </p>

            {/* form input untuk register / login */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Ini muncul ketika dia kondisi nya adalah register */}
            {!isLogin && (
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium text-gray-700">Full Name</label>
                        <input 
                            type="text"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value})}
                            placeholder="John Doe"
                            className="w-full mt-1 px-4 py-2 border rounded-md border-gray-400"
                        />
                    </div>
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium text-gray-700">Phone Number</label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 flex items-center justify-center pl-3 pt-1 text-gray-500 text-sm">
                                +62
                            </div>
                            <input 
                                type="text"
                                value={formData.number}
                                onChange={(e) => setFormData({ ...formData, number: e.target.value})}
                                placeholder="8xxxxxxx"
                                className="pl-12 w-full mt-1 px-4 py-2 border rounded-md border-gray-400"
                            />
                        </div>
                    </div>
                </div>
            )}
            {/* Ini muncul ketika dia kondisi nya itu login atau register */}
            <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                <input 
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value})}
                    placeholder="johndoe@gmail.com"
                    className="w-full mt-1 px-4 py-2 border rounded-md border-gray-400"
                />
            </div>

            <div>
                <label htmlFor="password" className="block text-sm font-medium text-gray-700">Password</label>
                <div className="relative">
                    <input 
                        type={showPassword ? "text" : "password"}
                        value={formData.password}
                        onChange={(e) => setFormData({ ...formData, password: e.target.value})}
                        placeholder="********"
                        className="w-full mt-1 px-4 py-2 border rounded-md border-gray-400"
                    />
                    <button
                        type="button"
                        className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
                        onClick={() => setShowPassword(!showPassword)}
                    >
                        {showPassword ? <AiOutlineEyeInvisible /> : <AiOutlineEye />}
                    </button>
                </div>
            </div>

            {errors.general && (
                <div className="text-red-500 text-sm mt-1">{errors.general}</div>
            )}

            {!isLogin && (
                <div className="flex items-center">
                    <input 
                        ref={termsCheckBoxRef}
                        id="terms"
                        type="checkbox"
                        className="h-4 w-4 text-indigo-600 border-gray-300 rounded"
                    />
                    <label htmlFor="terms" className="ml-2 block text-sm text-gray-700">
                        I agree to the {" "}
                        <button type="button" className="text-indigo-600 hover:underline" onClick={() => (setShowModal(!showModal))}> Term & Privacy Policy</button>
                    </label>
                </div>
            )}
            {errors.terms && <p className="text-red-500 text-xs mt-1">{errors.terms}</p>}

            {/* submit form */}
            <button
                type="submit"
                disabled={loading}
                className={`w-full py-2 px-4 rounded-md flex items-center justify-center gap-2 ${loading ? "bg-indigo-400 cursor-not-allowed text-white" : "bg-indigo-600 hover:bg-indigo-700 text-white"}`}
            >
                {loading ? (
                    <>
                        <LoadingSpinnerButton />
                        Processing...
                    </>
                ): (
                    isLogin ? "Let's Explore" : "Get Started"
                )}
            </button>


            {/* button untuk mengubah kondisi register dan login */}
            <p className="mt-6 text-sm text-center text-gray-600">
                {/* untuk mengubah kondisi auth nya (login/register) */}
                {isLogin ? (
                    <>
                        Don't have an account? {" "} 
                        <button 
                            type="button"
                            className="text-indigo-600 hover:underline"
                            onClick={() => {
                                setType("register")
                            }}
                        >
                            Sign Up
                        </button>
                    </>
                ) : (
                    <>
                        Already have an account? {" "} 
                        <button 
                            type="button"
                            className="text-indigo-600 hover:underline"
                            onClick={() => {
                                setType("login")
                            }}
                        >
                            Sign In
                        </button>
                    </>
                )}
            </p>
          </form>
        </div>


            {/* bagian kanan card */}
        <div className="hidden md:block bg-indigo-600 relative min-h-[750px] w-full">
            <Image 
                src="/images/auth-img.png"
                alt="Auth Image"
                fill
                className="object-cover"
                priority
            />
        </div>
      </div>
      {showModal && (
        <Modal
            type="information"
            message="By using this application, you agree to our Terms and Privacy Policy. We may collect usage data to improve your experience. We do not share your data with third parties without your consent. For full details, visit our legal page." 
            onOk={() => {
                setShowModal(false);
                if(termsCheckBoxRef.current) termsCheckBoxRef.current.checked = true;
            }}
            onCancel={() => setShowModal(false)}
        />
      )}
    </div>
  );
};

export default AuthPage;
