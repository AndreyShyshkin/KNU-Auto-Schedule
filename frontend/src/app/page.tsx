'use client';

import { useEffect, useState } from 'react';
import api from '@/lib/api';

interface SimpleEntity { id: number; name: string; }
interface Lesson {
    subject: SimpleEntity;
    teachers: SimpleEntity[];
    groups: SimpleEntity[];
    lessonType: string;
}
interface Auditorium { name: string; }
interface Appointment {
    id: number;
    lesson: Lesson;
    auditorium: Auditorium;
    dayOfWeek: number;
    timeSlot: number;
}

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
const SLOTS = [1, 2, 3, 4];

export default function Dashboard() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);

  const fetchSchedule = () => {
    setLoading(true);
    api.get('/appointments')
        .then(res => setAppointments(res.data))
        .finally(() => setLoading(false));
  };

  useEffect(() => { fetchSchedule(); }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    try {
        await api.post('/schedule/generate');
        fetchSchedule();
    } catch (err) {
        alert('Failed to generate schedule');
        console.error(err);
    } finally {
        setGenerating(false);
    }
  };

  const getAppointmentsForCell = (dayIndex: number, slot: number) => {
    // dayIndex 0..4, dayOfWeek 1..5
    return appointments.filter(a => a.dayOfWeek === dayIndex + 1 && a.timeSlot === slot);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">Schedule Dashboard</h1>
        <button 
            onClick={handleGenerate} 
            disabled={generating}
            className={`inline-flex items-center rounded-md px-4 py-2 text-sm text-white ${generating ? 'bg-gray-400' : 'bg-indigo-600 hover:bg-indigo-700'}`}
        >
          {generating ? 'Generating...' : 'Generate Schedule'}
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-gray-200 shadow">
        <div className="min-w-max">
            {/* Header */}
            <div className="grid grid-cols-[80px_repeat(5,1fr)] bg-gray-100 border-b border-gray-200">
                <div className="p-3 font-bold text-gray-500 text-center border-r">Time</div>
                {DAYS.map(day => (
                    <div key={day} className="p-3 font-bold text-gray-700 text-center border-r last:border-r-0">
                        {day}
                    </div>
                ))}
            </div>

            {/* Grid */}
            {SLOTS.map(slot => (
                <div key={slot} className="grid grid-cols-[80px_repeat(5,1fr)] border-b border-gray-200 last:border-b-0">
                    <div className="p-3 flex items-center justify-center font-semibold text-gray-500 border-r bg-gray-50">
                        Pair {slot}
                    </div>
                    {DAYS.map((day, dayIndex) => {
                        const cellAppts = getAppointmentsForCell(dayIndex, slot);
                        return (
                            <div key={`${day}-${slot}`} className="p-2 border-r last:border-r-0 min-h-[120px] bg-white relative">
                                {cellAppts.map(appt => (
                                    <div key={appt.id} className="mb-2 last:mb-0 p-2 rounded bg-blue-50 border border-blue-100 text-xs shadow-sm hover:shadow-md transition-shadow">
                                        <div className="font-bold text-blue-900 truncate" title={appt.lesson.subject.name}>
                                            {appt.lesson.subject.name}
                                        </div>
                                        <div className="text-blue-700 mt-1 flex flex-wrap gap-1">
                                             <span className="bg-white px-1 rounded border border-blue-200">{appt.lesson.lessonType}</span>
                                             <span className="bg-yellow-50 text-yellow-800 px-1 rounded border border-yellow-200">Room {appt.auditorium.name}</span>
                                        </div>
                                        <div className="mt-1 text-gray-600 truncate">
                                            {appt.lesson.teachers.map(t => t.name).join(', ')}
                                        </div>
                                        <div className="text-gray-500 truncate italic">
                                            {appt.lesson.groups.map(g => g.name).join(', ')}
                                        </div>
                                    </div>
                                ))}
                                {cellAppts.length === 0 && (
                                    <div className="absolute inset-0 flex items-center justify-center text-gray-300 pointer-events-none">
                                        -
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            ))}
        </div>
      </div>
    </div>
  );
}
