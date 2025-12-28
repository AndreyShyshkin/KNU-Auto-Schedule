'use client';

import { useEffect, useState } from 'react';
import api from '@/lib/api';
import { Plus, Trash2, Building } from 'lucide-react';

interface Faculty {
  id: number;
  name: string;
}

interface Chair {
  id: number;
  name: string;
  faculty: Faculty;
}

interface Speciality {
  id: number;
  name: string;
  faculty: Faculty;
}

export default function DepartmentsPage() {
  const [activeTab, setActiveTab] = useState<'faculties' | 'chairs' | 'specialities'>('faculties');
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [chairs, setChairs] = useState<Chair[]>([]);
  const [specialities, setSpecialities] = useState<Speciality[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [isCreating, setIsCreating] = useState(false);
  const [newName, setNewName] = useState('');
  const [selectedFacultyId, setSelectedFacultyId] = useState<number | ''>('');

  const fetchData = async () => {
    setLoading(true);
    try {
      const [fRes, cRes, sRes] = await Promise.all([
        api.get('/faculties'),
        api.get('/chairs'),
        api.get('/specialities')
      ]);
      setFaculties(fRes.data);
      setChairs(cRes.data);
      setSpecialities(sRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (activeTab === 'faculties') {
        await api.post('/faculties', { name: newName });
      } else if (activeTab === 'chairs') {
        await api.post('/chairs', { name: newName, faculty: { id: selectedFacultyId } });
      } else {
        await api.post('/specialities', { name: newName, faculty: { id: selectedFacultyId } });
      }
      setNewName('');
      setSelectedFacultyId('');
      setIsCreating(false);
      fetchData();
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete item?')) return;
    try {
      await api.delete(`/${activeTab}/${id}`);
      fetchData();
    } catch (err) { console.error(err); }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">Departments & Units</h1>
        <button onClick={() => setIsCreating(true)} className="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm text-white hover:bg-indigo-700">
          <Plus className="mr-2 h-4 w-4" /> Add {activeTab.slice(0, -1)}
        </button>
      </div>

      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {(['faculties', 'chairs', 'specialities'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => { setActiveTab(tab); setIsCreating(false); }}
              className={`${
                activeTab === tab
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
              } whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium capitalize`}
            >
              {tab}
            </button>
          ))}
        </nav>
      </div>

      {isCreating && (
        <form onSubmit={handleCreate} className="bg-white p-4 rounded-lg shadow border flex gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium">Name</label>
            <input type="text" value={newName} onChange={(e) => setNewName(e.target.value)} className="mt-1 block w-full rounded-md border p-2 sm:text-sm" required />
          </div>
          {activeTab !== 'faculties' && (
            <div className="flex-1">
              <label className="block text-sm font-medium">Faculty</label>
              <select 
                value={selectedFacultyId} 
                onChange={(e) => setSelectedFacultyId(Number(e.target.value))}
                className="mt-1 block w-full rounded-md border p-2 sm:text-sm"
                required
              >
                <option value="">Select Faculty...</option>
                {faculties.map(f => <option key={f.id} value={f.id}>{f.name}</option>)}
              </select>
            </div>
          )}
          <button type="submit" className="rounded-md bg-green-600 px-4 py-2 text-sm text-white hover:bg-green-700">Save</button>
          <button type="button" onClick={() => setIsCreating(false)} className="rounded-md bg-gray-200 px-4 py-2 text-sm text-gray-700 hover:bg-gray-300">Cancel</button>
        </form>
      )}

      <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Name</th>
              {activeTab !== 'faculties' && <th className="px-6 py-3 text-left text-xs font-medium uppercase text-gray-500">Faculty</th>}
              <th className="relative px-6 py-3"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {loading ? (
              <tr><td colSpan={3} className="px-6 py-4 text-center">Loading...</td></tr>
            ) : (
              (activeTab === 'faculties' ? faculties : activeTab === 'chairs' ? chairs : specialities).map((item) => (
                <tr key={item.id}>
                  <td className="px-6 py-4 text-sm font-medium text-gray-900 flex items-center">
                    <Building className="mr-2 h-4 w-4 text-gray-400" />
                    {item.name}
                  </td>
                  {activeTab !== 'faculties' && (
                    <td className="px-6 py-4 text-sm text-gray-500">
                        {(item as any).faculty?.name || 'N/A'}
                    </td>
                  )}
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
