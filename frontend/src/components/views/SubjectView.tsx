'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  fetchSubjects, createSubject, updateSubject, deleteSubject,
  Subject
} from '@/lib/api/scheduleApi';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { 
  Typography, Paper, Grid, Box, IconButton, Dialog, DialogTitle, DialogContent, 
  DialogActions, Button, TextField 
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';

export default function SubjectView() {
  const queryClient = useQueryClient();
  const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(null);

  const { data: subjects = [], isLoading } = useQuery({
    queryKey: ['subjects'],
    queryFn: fetchSubjects,
  });

  const createSubjectMutation = useMutation({
    mutationFn: createSubject,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['subjects'] }),
  });

  const updateSubjectMutation = useMutation({
    mutationFn: (data: { id: number; subject: Partial<Subject> }) => updateSubject(data.id, data.subject),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['subjects'] }),
  });

  const deleteSubjectMutation = useMutation({
    mutationFn: deleteSubject,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['subjects'] });
      setSelectedSubjectId(null);
    },
  });

  // UI State
  const [openDialog, setOpenDialog] = useState(false);
  const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create');
  const [formData, setFormData] = useState<Partial<Subject>>({});

  const handleAdd = () => {
    setFormData({});
    setDialogMode('create');
    setOpenDialog(true);
  };

  const handleEdit = () => {
    const subject = subjects.find(s => s.id === selectedSubjectId);
    if (subject) {
      setFormData(subject);
      setDialogMode('edit');
      setOpenDialog(true);
    }
  };

  const handleDelete = () => {
    if (selectedSubjectId && confirm('Delete this subject?')) {
      deleteSubjectMutation.mutate(selectedSubjectId);
    }
  };

  const handleSubmit = () => {
    if (dialogMode === 'create') {
      createSubjectMutation.mutate(formData);
    } else if (selectedSubjectId) {
      updateSubjectMutation.mutate({ id: selectedSubjectId, subject: formData });
    }
    setOpenDialog(false);
  };

  const columns: GridColDef[] = [
    { field: 'name', headerName: 'Subject Name', flex: 1 },
  ];

  return (
    <Grid container spacing={2} sx={{ height: '100%' }}>
      <Grid item xs={12} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">Subjects</Typography>
          <Box>
            <IconButton size="small" onClick={handleAdd}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedSubjectId} onClick={handleEdit}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedSubjectId} onClick={handleDelete}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={subjects}
            columns={columns}
            loading={isLoading}
            onRowClick={(params) => setSelectedSubjectId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedSubjectId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>{dialogMode === 'create' ? 'Add Subject' : 'Edit Subject'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            value={formData.name || ''}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmit}>Save</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  );
}