'use client';

import { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Plus, Trash2 } from 'lucide-react';

interface Group {
  id: number;
  name: string;
  courseYear: number;
  size: number;
}

export default function GroupsPage() {
  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [formData, setFormData] = useState({ name: '', courseYear: 1, size: 20 });

  const fetchGroups = () => {
    setLoading(true);
    api.get('/groups').then((res) => setGroups(res.data)).finally(() => setLoading(false));
  };

  useEffect(() => { fetchGroups(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await api.post('/groups', formData);
    setFormData({ name: '', courseYear: 1, size: 20 });
    setIsCreating(false);
    fetchGroups();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Delete group?')) {
      await api.delete(`/groups/${id}`);
      setGroups(groups.filter(g => g.id !== id));
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">Groups</h1>
        <button onClick={() => setIsCreating(true)} className="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700">
          <Plus className="mr-2 h-4 w-4" /> Add Group
        </button>
      </div>

      {isCreating && (
        <form onSubmit={handleCreate} className="bg-white p-4 rounded-lg shadow border flex gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium">Name</label>
            <input type="text" value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" placeholder="e.g. K-25" />
          </div>
          <div className="w-24">
            <label className="block text-sm font-medium">Course</label>
            <input type="number" value={formData.courseYear} onChange={(e) => setFormData({...formData, courseYear: parseInt(e.target.value)})} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" />
          </div>
          <div className="w-24">
            <label className="block text-sm font-medium">Size</label>
            <input type="number" value={formData.size} onChange={(e) => setFormData({...formData, size: parseInt(e.target.value)})} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" />
          </div>
          <button type="submit" className="rounded-md bg-green-600 px-4 py-2 text-sm text-white hover:bg-green-700">Save</button>
          <button type="button" onClick={() => setIsCreating(false)} className="rounded-md bg-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-300">Cancel</button>
        </form>
      )}

      <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Course</th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Size</th>
              <th className="relative px-6 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {loading ? (
              <tr><td colSpan={4} className="px-6 py-4 text-center">Loading...</td></tr>
            ) : (
              groups.map((group) => (
                <tr key={group.id}>
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{group.name}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{group.courseYear}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{group.size}</td>
                  <td className="px-6 py-4 text-right text-sm font-medium">
                    <button onClick={() => handleDelete(group.id)} className="text-red-600 hover:text-red-900"><Trash2 className="h-4 w-4" /></button>
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
