"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import {
  AiOutlineUser,
  AiOutlineSwap,
  AiOutlineLogout,
  AiOutlineAppstore,
  AiOutlineSolution,
  AiOutlineCalculator,
} from "react-icons/ai";
import { profile, logout } from "@/services/auth";

const Sidebar = () => {
  const rawPath = usePathname();
  const router = useRouter();
  const [pathname, setPathname] = useState<string | null>(null);
  const [initial, setInitial] = useState("U");

  useEffect(() => {
    setPathname(rawPath);

    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          router.push("/");
          return;
        }

        const res = await profile(token);
        const name = res.data?.name || "";
        if (name.length > 0) {
          setInitial(name[0].toUpperCase());
        }
      } catch (error) {
        console.error(error);
        logout();
        router.push("/");
      }
    };

    fetchProfile();
  }, [rawPath, router]);

  if (!pathname) return null;

  const navItems = [
    { href: "/dashboard", icon: <AiOutlineAppstore className="size-5 opacity-75" />, label: "Dashboard" },
    { href: "/dashboard/transaction", icon: <AiOutlineSwap className="size-5 opacity-75" />, label: "Transaction" },
    { href: "/dashboard/budget", icon: <AiOutlineCalculator className="size-5 opacity-75" />, label: "Budget" },
    { href: "/dashboard/goals", icon: <AiOutlineSolution className="size-5 opacity-75" />, label: "Goals" },
    { href: "/dashboard/summary", icon: <AiOutlineSolution className="size-5 opacity-75" />, label: "Summary" },
    { href: "/dashboard/profile", icon: <AiOutlineUser className="size-5 opacity-75" />, label: "Profile" },
  ];

  return (
    <div className="flex fixed top-0 left-0 h-screen w-16 flex-col justify-between border-e border-gray-50 bg-white">
      <div>
        <div className="inline-flex size-16 items-center justify-center">
          <span className="grid size-10 place-content-center rounded-lg bg-gray-100 text-xs text-gray-600">
            {initial}
          </span>
        </div>

        <div className="border-t border-gray-100">
          <div className="px-2">
            <div className="py-4">
              {navItems.slice(0, 1).map(({ href, icon, label }) => {
                const isActive = pathname === href;
                return (
                  <Link
                    key={href}
                    href={href}
                    className={`group relative flex justify-center rounded-sm px-2 py-1.5 ${
                      isActive
                        ? "bg-blue-50 text-blue-700"
                        : "text-gray-500 hover:bg-gray-50 hover:text-gray-700"
                    }`}
                  >
                    {icon}
                    <span className="invisible absolute start-full top-1/2 ms-4 -translate-y-1/2 rounded-sm bg-gray-900 px-2 py-1.5 text-xs font-medium text-white group-hover:visible">
                      {label}
                    </span>
                  </Link>
                );
              })}
            </div>

            <ul className="space-y-1 border-t border-gray-100 pt-4">
              {navItems.slice(1).map(({ href, icon, label }) => {
                const isActive = pathname === href;
                return (
                  <li key={href}>
                    <Link
                      href={href}
                      className={`group relative flex justify-center rounded-sm px-2 py-1.5 ${
                        isActive
                          ? "bg-blue-50 text-blue-700"
                          : "text-gray-500 hover:bg-gray-50 hover:text-gray-700"
                      }`}
                    >
                      {icon}
                      <span className="invisible absolute start-full top-1/2 ms-4 -translate-y-1/2 rounded-sm bg-gray-900 px-2 py-1.5 text-xs font-medium text-white group-hover:visible">
                        {label}
                      </span>
                    </Link>
                  </li>
                );
              })}
            </ul>
          </div>
        </div>
      </div>

      <div className="sticky inset-x-0 bottom-0 border-t border-gray-100 bg-white p-2">
        <button
          onClick={() => {
            logout();
            window.location.href = "/";
          }}
          className="group relative flex w-full justify-center rounded-lg px-2 py-1.5 text-sm text-red-500 hover:bg-gray-50 hover:text-gray-700"
        >
          <AiOutlineLogout className="size-5 opacity-75" />
          <span className="invisible absolute start-full top-1/2 ms-4 -translate-y-1/2 rounded-sm bg-gray-900 px-2 py-1.5 text-xs font-medium text-white group-hover:visible">
            Logout
          </span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
