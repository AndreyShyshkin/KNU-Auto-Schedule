'use client';

import { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Plus, Trash2 } from 'lucide-react';

interface Auditorium {
  id: number;
  name: string;
  type: string;
  capacity: number;
}

export default function AuditoriumsPage() {
  const [items, setItems] = useState<Auditorium[]>([]);
  const [loading, setLoading] = useState(true);
  const [isCreating, setIsCreating] = useState(false);
  const [formData, setFormData] = useState({ name: '', type: 'Lecture', capacity: 30 });

  const fetch = () => {
    setLoading(true);
    api.get('/auditoriums').then((res) => setItems(res.data)).finally(() => setLoading(false));
  };

  useEffect(() => { fetch(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await api.post('/auditoriums', formData);
    setFormData({ name: '', type: 'Lecture', capacity: 30 });
    setIsCreating(false);
    fetch();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Delete auditorium?')) {
      await api.delete(`/auditoriums/${id}`);
      setItems(items.filter(i => i.id !== id));
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">Auditoriums</h1>
        <button onClick={() => setIsCreating(true)} className="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700">
          <Plus className="mr-2 h-4 w-4" /> Add Auditorium
        </button>
      </div>

      {isCreating && (
        <form onSubmit={handleCreate} className="bg-white p-4 rounded-lg shadow border flex gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium">Name</label>
            <input type="text" value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" placeholder="e.g. 101" />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium">Type</label>
            <select value={formData.type} onChange={(e) => setFormData({...formData, type: e.target.value})} className="mt-1 block w-full rounded-md border p-2 sm:text-sm">
                <option value="Lecture">Lecture</option>
                <option value="Lab">Laboratory</option>
                <option value="Computer">Computer Class</option>
            </select>
          </div>
          <div className="w-24">
            <label className="block text-sm font-medium">Capacity</label>
            <input type="number" value={formData.capacity} onChange={(e) => setFormData({...formData, capacity: parseInt(e.target.value)})} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" />
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
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Type</th>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Capacity</th>
              <th className="relative px-6 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {loading ? (
              <tr><td colSpan={4} className="px-6 py-4 text-center">Loading...</td></tr>
            ) : (
              items.map((item) => (
                <tr key={item.id}>
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{item.name}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{item.type}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{item.capacity}</td>
                  <td className="px-6 py-4 text-right text-sm font-medium">
                    <button onClick={() => handleDelete(item.id)} className="text-red-600 hover:text-red-900"><Trash2 className="h-4 w-4" /></button>
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
