'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  fetchEarmarks, createEarmark, updateEarmark, deleteEarmark,
  fetchAuditoriums, createAuditorium, updateAuditorium, deleteAuditorium,
  Earmark, Auditorium
} from '@/lib/api/scheduleApi';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { 
  Typography, Paper, Grid, Box, IconButton, Dialog, DialogTitle, DialogContent, 
  DialogActions, Button, TextField 
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';

export default function PlacementView() {
  const queryClient = useQueryClient();
  const [selectedEarmarkId, setSelectedEarmarkId] = useState<number | null>(null);
  const [selectedAuditoriumId, setSelectedAuditoriumId] = useState<number | null>(null);

  // --- Earmarks ---
  const { data: earmarks = [], isLoading: isLoadingEarmarks } = useQuery({
    queryKey: ['earmarks'],
    queryFn: fetchEarmarks,
  });

  const createEarmarkMutation = useMutation({
    mutationFn: createEarmark,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['earmarks'] }),
  });

  const updateEarmarkMutation = useMutation({
    mutationFn: (data: { id: number; earmark: Partial<Earmark> }) => updateEarmark(data.id, data.earmark),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['earmarks'] }),
  });

  const deleteEarmarkMutation = useMutation({
    mutationFn: deleteEarmark,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['earmarks'] });
      setSelectedEarmarkId(null);
    },
  });

  // --- Auditoriums ---
  const { data: auditoriums = [], isLoading: isLoadingAuditoriums } = useQuery({
    queryKey: ['auditoriums'],
    queryFn: fetchAuditoriums,
  });

  const createAuditoriumMutation = useMutation({
    mutationFn: createAuditorium,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['auditoriums'] }),
  });

  const updateAuditoriumMutation = useMutation({
    mutationFn: (data: { id: number; auditorium: Partial<Auditorium> }) => updateAuditorium(data.id, data.auditorium),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['auditoriums'] }),
  });

  const deleteAuditoriumMutation = useMutation({
    mutationFn: deleteAuditorium,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['auditoriums'] });
      setSelectedAuditoriumId(null);
    },
  });

  // UI State
  const [openEarmarkDialog, setOpenEarmarkDialog] = useState(false);
  const [earmarkDialogMode, setEarmarkDialogMode] = useState<'create' | 'edit'>('create');
  const [earmarkFormData, setEarmarkFormData] = useState<Partial<Earmark>>({});

  const [openAuditoriumDialog, setOpenAuditoriumDialog] = useState(false);
  const [auditoriumDialogMode, setAuditoriumDialogMode] = useState<'create' | 'edit'>('create');
  const [auditoriumFormData, setAuditoriumFormData] = useState<Partial<Auditorium>>({});

  // Handlers Earmark
  const handleAddEarmark = () => {
    setEarmarkFormData({ size: 30 });
    setEarmarkDialogMode('create');
    setOpenEarmarkDialog(true);
  };

  const handleEditEarmark = () => {
    const earmark = earmarks.find(e => e.id === selectedEarmarkId);
    if (earmark) {
      setEarmarkFormData(earmark);
      setEarmarkDialogMode('edit');
      setOpenEarmarkDialog(true);
    }
  };

  const handleDeleteEarmark = () => {
    if (selectedEarmarkId && confirm('Delete this earmark?')) {
      deleteEarmarkMutation.mutate(selectedEarmarkId);
    }
  };

  const handleSubmitEarmark = () => {
    if (earmarkDialogMode === 'create') {
      createEarmarkMutation.mutate(earmarkFormData);
    } else if (selectedEarmarkId) {
      updateEarmarkMutation.mutate({ id: selectedEarmarkId, earmark: earmarkFormData });
    }
    setOpenEarmarkDialog(false);
  };

  // Handlers Auditorium
  const handleAddAuditorium = () => {
    if (!selectedEarmarkId) return;
    setAuditoriumFormData({ earmarkId: selectedEarmarkId });
    setAuditoriumDialogMode('create');
    setOpenAuditoriumDialog(true);
  };

  const handleEditAuditorium = () => {
    const auditorium = auditoriums.find(a => a.id === selectedAuditoriumId);
    if (auditorium) {
      setAuditoriumFormData(auditorium);
      setAuditoriumDialogMode('edit');
      setOpenAuditoriumDialog(true);
    }
  };

  const handleDeleteAuditorium = () => {
    if (selectedAuditoriumId && confirm('Delete this auditorium?')) {
      deleteAuditoriumMutation.mutate(selectedAuditoriumId);
    }
  };

  const handleSubmitAuditorium = () => {
    if (auditoriumDialogMode === 'create') {
      createAuditoriumMutation.mutate(auditoriumFormData);
    } else if (selectedAuditoriumId) {
      updateAuditoriumMutation.mutate({ id: selectedAuditoriumId, auditorium: auditoriumFormData });
    }
    setOpenAuditoriumDialog(false);
  };

  const filteredAuditoriums = selectedEarmarkId 
    ? auditoriums.filter(a => a.earmarkId === selectedEarmarkId)
    : [];

  const earmarkColumns: GridColDef[] = [
    { field: 'name', headerName: 'Earmark Name', flex: 1 },
    { field: 'size', headerName: 'Size', width: 70 },
  ];

  const auditoriumColumns: GridColDef[] = [
    { field: 'name', headerName: 'Auditorium Name', flex: 1 },
  ];

  return (
    <Grid container spacing={2} sx={{ height: '100%' }}>
      {/* Left Pane: Earmarks */}
      <Grid item xs={6} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">Earmarks</Typography>
          <Box>
            <IconButton size="small" onClick={handleAddEarmark}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedEarmarkId} onClick={handleEditEarmark}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedEarmarkId} onClick={handleDeleteEarmark}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={earmarks}
            columns={earmarkColumns}
            loading={isLoadingEarmarks}
            onRowClick={(params) => setSelectedEarmarkId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedEarmarkId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      {/* Right Pane: Auditoriums */}
      <Grid item xs={6} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="subtitle1">
            {selectedEarmarkId ? 'Auditoriums' : 'Select an Earmark'}
          </Typography>
          <Box>
            <IconButton size="small" disabled={!selectedEarmarkId} onClick={handleAddAuditorium}><AddIcon /></IconButton>
            <IconButton size="small" disabled={!selectedAuditoriumId} onClick={handleEditAuditorium}><EditIcon /></IconButton>
            <IconButton size="small" disabled={!selectedAuditoriumId} onClick={handleDeleteAuditorium}><DeleteIcon /></IconButton>
          </Box>
        </Box>
        <Paper sx={{ flexGrow: 1 }}>
          <DataGrid
            rows={filteredAuditoriums}
            columns={auditoriumColumns}
            loading={isLoadingAuditoriums}
            onRowClick={(params) => setSelectedAuditoriumId(params.row.id as number)}
            density="compact"
            hideFooter
            getRowClassName={(params) => params.row.id === selectedAuditoriumId ? 'Mui-selected' : ''}
            sx={{ '& .Mui-selected': { bgcolor: 'primary.light', '&:hover': { bgcolor: 'primary.light' } } }}
          />
        </Paper>
      </Grid>

      {/* Earmark Dialog */}
      <Dialog open={openEarmarkDialog} onClose={() => setOpenEarmarkDialog(false)}>
        <DialogTitle>{earmarkDialogMode === 'create' ? 'Add Earmark' : 'Edit Earmark'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            value={earmarkFormData.name || ''}
            onChange={(e) => setEarmarkFormData({ ...earmarkFormData, name: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Size"
            type="number"
            fullWidth
            value={earmarkFormData.size || ''}
            onChange={(e) => setEarmarkFormData({ ...earmarkFormData, size: parseInt(e.target.value) })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenEarmarkDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmitEarmark}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* Auditorium Dialog */}
      <Dialog open={openAuditoriumDialog} onClose={() => setOpenAuditoriumDialog(false)}>
        <DialogTitle>{auditoriumDialogMode === 'create' ? 'Add Auditorium' : 'Edit Auditorium'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Name"
            fullWidth
            value={auditoriumFormData.name || ''}
            onChange={(e) => setAuditoriumFormData({ ...auditoriumFormData, name: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenAuditoriumDialog(false)}>Cancel</Button>
          <Button onClick={handleSubmitAuditorium}>Save</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  );
}