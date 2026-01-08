'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  fetchFaculties, createFaculty, updateFaculty, deleteFaculty,
  fetchChairs, createChair, updateChair, deleteChair,
  Faculty, Chair
} from '@/lib/api/scheduleApi';
import { DataGrid, GridColDef, GridRowSelectionModel } from '@mui/x-data-grid';
import { 
  Typography, Paper, Grid, Box, Button, IconButton, 
  Dialog, DialogTitle, DialogContent, DialogActions, TextField 
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';

export default function ChairView() {
  const queryClient = useQueryClient();
  const [selectedFacultyId, setSelectedFacultyId] = useState<number | null>(null);
  const [selectedChairId, setSelectedChairId] = useState<number | null>(null);

  // --- Faculties ---
  const { data: faculties = [], isLoading: isLoadingFaculties } = useQuery({
    queryKey: ['faculties'],
    queryFn: fetchFaculties,
  });

  const createFacultyMutation = useMutation({
    mutationFn: createFaculty,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['faculties'] }),
  });

  const updateFacultyMutation = useMutation({
    mutationFn: (data: { id: number; faculty: Partial<Faculty> }) => updateFaculty(data.id, data.faculty),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['faculties'] }),
  });

  const deleteFacultyMutation = useMutation({
    mutationFn: deleteFaculty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['faculties'] });
      setSelectedFacultyId(null);
    },
  });

  // --- Chairs ---
  const { data: chairs = [], isLoading: isLoadingChairs } = useQuery({
    queryKey: ['chairs'],
    queryFn: fetchChairs,
  });

  const createChairMutation = useMutation({
    mutationFn: createChair,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['chairs'] }),
  });

  const updateChairMutation = useMutation({
    mutationFn: (data: { id: number; chair: Partial<Chair> }) => updateChair(data.id, data.chair),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['chairs'] }),
  });

  const deleteChairMutation = useMutation({
    mutationFn: deleteChair,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['chairs'] });
      setSelectedChairId(null);
    },
  });

  // --- UI State ---
  const [openFacultyDialog, setOpenFacultyDialog] = useState(false);
  const [facultyDialogMode, setFacultyDialogMode] = useState<'create' | 'edit'>('create');
  const [facultyFormData, setFacultyFormData] = useState<Partial<Faculty>>({});

  const [openChairDialog, setOpenChairDialog] = useState(false);
  const [chairDialogMode, setChairDialogMode] = useState<'create' | 'edit'>('create');
  const [chairFormData, setChairFormData] = useState<Partial<Chair>>({});

  // --- Handlers Faculty ---
  const handleAddFaculty = () => {
    setFacultyFormData({});
    setFacultyDialogMode('create');
    setOpenFacultyDialog(true);
  };

  const handleEditFaculty = () => {
    const faculty = faculties.find(f => f.id === selectedFacultyId);
    if (faculty) {
      setFacultyFormData(faculty);
      setFacultyDialogMode('edit');
      setOpenFacultyDialog(true);
    }
  };

  const handleDeleteFaculty = () => {
    if (selectedFacultyId && confirm('Are you sure you want to delete this faculty?')) {
      deleteFacultyMutation.mutate(selectedFacultyId);
    }
  };

  const handleSubmitFaculty = () => {
    if (facultyDialogMode === 'create') {
      createFacultyMutation.mutate(facultyFormData);
    } else if (selectedFacultyId) {
      updateFacultyMutation.mutate({ id: selectedFacultyId, faculty: facultyFormData });
    }
    setOpenFacultyDialog(false);
  };

  // --- Handlers Chair ---
  const handleAddChair = () => {
    if (!selectedFacultyId) return;
    setChairFormData({ facultyId: selectedFacultyId });
    setChairDialogMode('create');
    setOpenChairDialog(true);
  };

  const handleEditChair = () => {
    const chair = chairs.find(c => c.id === selectedChairId);
    if (chair) {
      setChairFormData(chair);
      setChairDialogMode('edit');
      setOpenChairDialog(true);
    }
  };

  const handleDeleteChair = () => {
    if (selectedChairId && confirm('Are you sure you want to delete this chair?')) {
      deleteChairMutation.mutate(selectedChairId);
    }
  };

  const handleSubmitChair = () => {
    if (chairDialogMode === 'create') {
      createChairMutation.mutate(chairFormData);
    } else if (selectedChairId) {
      updateChairMutation.mutate({ id: selectedChairId, chair: chairFormData });
    }
    setOpenChairDialog(false);
  };

  const filteredChairs = selectedFacultyId 
    ? chairs.filter(c => c.facultyId === selectedFacultyId)
    : [];

  const facultyColumns: GridColDef[] = [
    { field: 'name', headerName: 'Faculty Name', flex: 1 },
    { field: 'description', headerName: 'Description', flex: 1 },
  ];

  const chairColumns: GridColDef[] = [
    { field: 'name', headerName: 'Chair Name', flex: 1 },
    { field: 'description', headerName: 'Description', flex: 1 },
  ];

  return (
    <Grid container spacing={2} sx={{ height: '100%' }}>
      {/* Left Pane: Faculties */}
      <Grid item xs={6} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">Faculties</Typography>
          <Box>
            <IconButton size="small" onClick={handleAddFaculty}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedFacultyId} onClick={handleEditFaculty}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedFacultyId} onClick={handleDeleteFaculty}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={faculties}
            columns={facultyColumns}
            loading={isLoadingFaculties}
            onRowClick={(params) => setSelectedFacultyId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedFacultyId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      {/* Right Pane: Chairs */}
      <Grid item xs={6} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">
            {selectedFacultyId ? 'Chairs' : 'Select a Faculty'}
          </Typography>
          <Box>
            <IconButton size="small" disabled={!selectedFacultyId} onClick={handleAddChair}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedChairId} onClick={handleEditChair}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedChairId} onClick={handleDeleteChair}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={filteredChairs}
            columns={chairColumns}
            loading={isLoadingChairs}
            onRowClick={(params) => setSelectedChairId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedChairId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      {/* Faculty Dialog */}
      <Dialog open={openFacultyDialog} onClose={() => setOpenFacultyDialog(false)}>
        <DialogTitle>{facultyDialogMode === 'create' ? 'Add Faculty' : 'Edit Faculty'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            value={facultyFormData.name || ''}
            onChange={(e) => setFacultyFormData({ ...facultyFormData, name: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Description"
            fullWidth
            value={facultyFormData.description || ''}
            onChange={(e) => setFacultyFormData({ ...facultyFormData, description: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenFacultyDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmitFaculty}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* Chair Dialog */}
      <Dialog open={openChairDialog} onClose={() => setOpenChairDialog(false)}>
        <DialogTitle>{chairDialogMode === 'create' ? 'Add Chair' : 'Edit Chair'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            value={chairFormData.name || ''}
            onChange={(e) => setChairFormData({ ...chairFormData, name: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Description"
            fullWidth
            value={chairFormData.description || ''}
            onChange={(e) => setChairFormData({ ...chairFormData, description: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenChairDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmitChair}>Save</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  );
}