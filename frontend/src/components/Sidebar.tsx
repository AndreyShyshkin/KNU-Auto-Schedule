'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  LayoutDashboard, 
  Users, 
  GraduationCap, 
  BookOpen, 
  Building2, 
  CalendarDays, 
  Settings,
  Library,
  Network
} from 'lucide-react';
import { cn } from '@/lib/utils'; // We need to create this utility

const navigation = [
  { name: 'Dashboard', href: '/', icon: LayoutDashboard },
  { name: 'Teachers', href: '/teachers', icon: Users },
  { name: 'Groups', href: '/groups', icon: GraduationCap },
  { name: 'Subjects', href: '/subjects', icon: BookOpen },
  { name: 'Auditoriums', href: '/auditoriums', icon: Building2 },
  { name: 'Departments', href: '/departments', icon: Network }, // Chairs/Specialities
  { name: 'Lessons (Load)', href: '/lessons', icon: Library },
  { name: 'Settings', href: '/settings', icon: Settings },
];

export function Sidebar() {
  const pathname = usePathname();

  return (
    <div className="flex h-full w-64 flex-col bg-slate-900 text-white">
      <div className="flex h-16 items-center px-6 font-bold text-xl tracking-wider border-b border-slate-800">
        KNU Schedule
      </div>
      <nav className="flex-1 space-y-1 px-2 py-4">
        {navigation.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.name}
              href={item.href}
              className={cn(
                isActive
                  ? 'bg-slate-800 text-white'
                  : 'text-slate-400 hover:bg-slate-800 hover:text-white',
                'group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors'
              )}
            >
              <item.icon
                className={cn(
                  isActive ? 'text-white' : 'text-slate-400 group-hover:text-white',
                  'mr-3 h-5 w-5 flex-shrink-0'
                )}
                aria-hidden="true"
              />
              {item.name}
            </Link>
          );
        })}
      </nav>
      <div className="p-4 border-t border-slate-800">
         <p className="text-xs text-slate-500">v0.1 Migration</p>
      </div>
    </div>
  );
}
