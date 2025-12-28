'use client';

import { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Plus, Trash2 } from 'lucide-react';

interface Subject {
  id: number;
  name: string;
}

export default function SubjectsPage() {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [newName, setNewName] = useState('');

  const fetch = () => {
    setLoading(true);
    api.get('/subjects').then((res) => setSubjects(res.data)).finally(() => setLoading(false));
  };

  useEffect(() => { fetch(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await api.post('/subjects', { name: newName });
    setNewName('');
    setIsCreating(false);
    fetch();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Delete subject?')) {
      await api.delete(`/subjects/${id}`);
      setSubjects(subjects.filter(s => s.id !== id));
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">Subjects</h1>
        <button onClick={() => setIsCreating(true)} className="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700">
          <Plus className="mr-2 h-4 w-4" /> Add Subject
        </button>
      </div>

      {isCreating && (
        <form onSubmit={handleCreate} className="bg-white p-4 rounded-lg shadow border flex gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium">Subject Name</label>
            <input type="text" value={newName} onChange={(e) => setNewName(e.target.value)} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" placeholder="e.g. Mathematics" />
          </div>
          <button type="submit" className="rounded-md bg-green-600 px-4 py-2 text-sm text-white hover:bg-green-700">Save</button>
          <button type="button" onClick={() => setIsCreating(false)} className="rounded-md bg-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-300">Cancel</button>
        </form>
      )}

      <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Name</th>
              <th className="relative px-6 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {loading ? (
              <tr><td colSpan={3} className="px-6 py-4 text-center">Loading...</td></tr>
            ) : (
              subjects.map((s) => (
                <tr key={s.id}>
                  <td className="px-6 py-4 text-sm text-gray-500">{s.id}</td>
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{s.name}</td>
                  <td className="px-6 py-4 text-right text-sm font-medium">
                    <button onClick={() => handleDelete(s.id)} className="text-red-600 hover:text-red-900"><Trash2 className="h-4 w-4" /></button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
