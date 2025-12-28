'use client';

import { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Plus, Trash2, BookOpen } from 'lucide-react';

interface SimpleEntity { id: number; name: string; }
interface Lesson {
  id: number;
  subject: SimpleEntity;
  lessonType: string;
  durationHours: number;
  teachers: SimpleEntity[];
  groups: SimpleEntity[];
}

export default function LessonsPage() {
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [subjects, setSubjects] = useState<SimpleEntity[]>([]);
  const [teachers, setTeachers] = useState<SimpleEntity[]>([]);
  const [groups, setGroups] = useState<SimpleEntity[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  
  // Form State
  const [formData, setFormData] = useState({
    subjectId: '',
    lessonType: 'Lecture',
    durationHours: 2,
    teacherIds: [] as string[],
    groupIds: [] as string[]
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [l, s, t, g] = await Promise.all([
        api.get('/lessons'),
        api.get('/subjects'),
        api.get('/teachers'),
        api.get('/groups')
      ]);
      setLessons(l.data);
      setSubjects(s.data);
      setTeachers(t.data);
      setGroups(g.data);
    } catch (err) { console.error(err); } 
    finally { setLoading(false); }
  };

  useEffect(() => { fetchData(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/lessons', {
        ...formData,
        subjectId: Number(formData.subjectId),
        teacherIds: formData.teacherIds.map(Number),
        groupIds: formData.groupIds.map(Number)
      });
      setIsCreating(false);
      setFormData({ subjectId: '', lessonType: 'Lecture', durationHours: 2, teacherIds: [], groupIds: [] });
      fetchData();
    } catch (err) { console.error(err); }
  };

  const handleDelete = async (id: number) => {
    if(confirm('Delete lesson?')) {
        await api.delete(`/lessons/${id}`);
        setLessons(lessons.filter(l => l.id !== id));
    }
  }

  // Helper for multiple select change
  const handleMultiChange = (e: React.ChangeEvent<HTMLSelectElement>, field: 'teacherIds' | 'groupIds') => {
    const values = Array.from(e.target.selectedOptions, option => option.value);
    setFormData({ ...formData, [field]: values });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">Academic Load (Lessons)</h1>
        <button onClick={() => setIsCreating(true)} className="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700">
          <Plus className="mr-2 h-4 w-4" /> Add Lesson
        </button>
      </div>

      {isCreating && (
        <form onSubmit={handleCreate} className="bg-white p-6 rounded-lg shadow border grid grid-cols-1 md:grid-cols-2 gap-6">
          
          <div>
            <label className="block text-sm font-medium">Subject</label>
            <select 
                className="mt-1 block w-full rounded-md border p-2"
                value={formData.subjectId}
                onChange={e => setFormData({...formData, subjectId: e.target.value})}
                required
            >
                <option value="">Select Subject...</option>
                {subjects.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>

          <div>
             <label className="block text-sm font-medium">Type</label>
             <select 
                className="mt-1 block w-full rounded-md border p-2"
                value={formData.lessonType}
                onChange={e => setFormData({...formData, lessonType: e.target.value})}
             >
                <option value="Lecture">Lecture</option>
                <option value="Practical">Practical</option>
                <option value="Lab">Laboratory</option>
                <option value="Seminar">Seminar</option>
             </select>
          </div>

          <div>
            <label className="block text-sm font-medium">Teachers (Hold Ctrl/Cmd to select multiple)</label>
            <select 
                multiple
                className="mt-1 block w-full rounded-md border p-2 h-32"
                value={formData.teacherIds}
                onChange={e => handleMultiChange(e, 'teacherIds')}
            >
                {teachers.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium">Groups (Hold Ctrl/Cmd to select multiple)</label>
            <select 
                multiple
                className="mt-1 block w-full rounded-md border p-2 h-32"
                value={formData.groupIds}
                onChange={e => handleMultiChange(e, 'groupIds')}
            >
                {groups.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
            </select>
          </div>

          <div className="md:col-span-2 flex justify-end gap-3 pt-4 border-t">
            <button type="button" onClick={() => setIsCreating(false)} className="rounded-md bg-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-300">Cancel</button>
            <button type="submit" className="rounded-md bg-green-600 px-4 py-2 text-sm text-white hover:bg-green-700">Save Lesson</button>
          </div>
        </form>
      )}

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {loading ? <p>Loading...</p> : lessons.map((lesson) => (
            <div key={lesson.id} className="relative flex flex-col justify-between rounded-lg border border-gray-200 bg-white p-6 shadow-sm hover:shadow-md transition-shadow">
                <div>
                    <div className="flex items-center justify-between mb-4">
                        <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${lesson.lessonType === 'Lecture' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'}`}>
                            {lesson.lessonType}
                        </span>
                        <button onClick={() => handleDelete(lesson.id)} className="text-gray-400 hover:text-red-500"><Trash2 className="h-4 w-4" /></button>
                    </div>
                    <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                        <BookOpen className="h-5 w-5 text-gray-500" />
                        {lesson.subject?.name || 'Unknown Subject'}
                    </h3>
                    
                    <div className="mt-4 space-y-2 text-sm text-gray-600">
                        <p>
                            <span className="font-semibold text-gray-900">Teachers: </span> 
                            {lesson.teachers.map(t => t.name).join(', ') || 'None'}
                        </p>
                        <p>
                            <span className="font-semibold text-gray-900">Groups: </span> 
                            {lesson.groups.map(g => g.name).join(', ') || 'None'}
                        </p>
                    </div>
                </div>
            </div>
        ))}
      </div>
    </div>
  );
}
