'use client'

import {
	Auditorium,
	createAuditorium,
	createEarmark,
	deleteAuditorium,
	deleteEarmark,
	Earmark,
	fetchAuditoriums,
	fetchEarmarks,
	fetchBuildings,
	updateAuditorium,
	updateEarmark,
	Building,
} from '@/lib/api/scheduleApi'
import {
	Add as AddIcon,
	Delete as DeleteIcon,
	Edit as EditIcon,
} from '@mui/icons-material'
import {
	Box,
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogTitle,
	Grid,
	IconButton,
	Paper,
	TextField,
	Typography,
	FormControl,
	InputLabel,
	Select,
	MenuItem,
} from '@mui/material'
import { DataGrid, GridColDef } from '@mui/x-data-grid'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

export default function PlacementView() {
	const queryClient = useQueryClient()
	const [selectedBuildingId, setSelectedBuildingId] = useState<number | null>(null)
	const [selectedEarmarkId, setSelectedEarmarkId] = useState<number | null>(null)
	const [selectedAuditoriumId, setSelectedAuditoriumId] = useState<number | null>(null)

	// Fetch Data
	const { data: buildings = [] } = useQuery({
		queryKey: ['buildings'],
		queryFn: fetchBuildings,
	})
	const { data: earmarks = [], isLoading: isLoadingEarmarks } = useQuery({
		queryKey: ['earmarks'],
		queryFn: fetchEarmarks,
	})
	const { data: auditoriums = [], isLoading: isLoadingAuditoriums } = useQuery({
		queryKey: ['auditoriums'],
		queryFn: fetchAuditoriums,
	})

	// --- Earmark Mutations ---
	const createEarmarkMutation = useMutation({
		mutationFn: createEarmark,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['earmarks'] }),
	})
	const updateEarmarkMutation = useMutation({
		mutationFn: (data: { id: number; earmark: Partial<Earmark> }) =>
			updateEarmark(data.id, data.earmark),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['earmarks'] }),
	})
	const deleteEarmarkMutation = useMutation({
		mutationFn: deleteEarmark,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['earmarks'] })
			setSelectedEarmarkId(null)
		},
	})

	// --- Auditorium Mutations ---
	const createAuditoriumMutation = useMutation({
		mutationFn: createAuditorium,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['auditoriums'] }),
	})
	const updateAuditoriumMutation = useMutation({
		mutationFn: (data: { id: number; auditorium: Partial<Auditorium> }) =>
			updateAuditorium(data.id, data.auditorium),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['auditoriums'] }),
	})
	const deleteAuditoriumMutation = useMutation({
		mutationFn: deleteAuditorium,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['auditoriums'] })
			setSelectedAuditoriumId(null)
		},
	})

	// --- UI Dialog States ---
	const [openEarmarkDialog, setOpenEarmarkDialog] = useState(false)
	const [earmarkDialogMode, setEarmarkDialogMode] = useState<'create' | 'edit'>('create')
	const [earmarkFormData, setEarmarkFormData] = useState<Partial<Earmark>>({})

	const [openAuditoriumDialog, setOpenAuditoriumDialog] = useState(false)
	const [auditoriumDialogMode, setAuditoriumDialogMode] = useState<'create' | 'edit'>('create')
	const [auditoriumFormData, setAuditoriumFormData] = useState<Partial<Auditorium>>({})

	// Handlers Earmark
	const handleAddEarmark = () => {
		setEarmarkFormData({ buildingId: selectedBuildingId || undefined, size: 30 })
		setEarmarkDialogMode('create')
		setOpenEarmarkDialog(true)
	}
	const handleEditEarmark = () => {
		const em = earmarks.find(e => e.id === selectedEarmarkId)
		if (em) { setEarmarkFormData(em); setEarmarkDialogMode('edit'); setOpenEarmarkDialog(true); }
	}
	const handleDeleteEarmark = () => { if (selectedEarmarkId && confirm('Видалити цей тип?')) deleteEarmarkMutation.mutate(selectedEarmarkId); }
	const handleSubmitEarmark = () => {
		if (earmarkDialogMode === 'create') createEarmarkMutation.mutate(earmarkFormData);
		else if (selectedEarmarkId) updateEarmarkMutation.mutate({ id: selectedEarmarkId, earmark: earmarkFormData });
		setOpenEarmarkDialog(false)
	}

	// Handlers Auditorium
	const handleAddAuditorium = () => {
		setAuditoriumFormData({ buildingId: selectedBuildingId || undefined, earmarkId: selectedEarmarkId || undefined })
		setAuditoriumDialogMode('create')
		setOpenAuditoriumDialog(true)
	}
	const handleEditAuditorium = () => {
		const aud = auditoriums.find(a => a.id === selectedAuditoriumId)
		if (aud) { setAuditoriumFormData(aud); setAuditoriumDialogMode('edit'); setOpenAuditoriumDialog(true); }
	}
	const handleDeleteAuditorium = () => { if (selectedAuditoriumId && confirm('Видалити аудиторію?')) deleteAuditoriumMutation.mutate(selectedAuditoriumId); }
	const handleSubmitAuditorium = () => {
		if (auditoriumDialogMode === 'create') createAuditoriumMutation.mutate(auditoriumFormData);
		else if (selectedAuditoriumId) updateAuditoriumMutation.mutate({ id: selectedAuditoriumId, auditorium: auditoriumFormData });
		setOpenAuditoriumDialog(false)
	}

	// Filtered Lists
	const filteredEarmarks = selectedBuildingId ? earmarks.filter(e => e.buildingId === selectedBuildingId) : []
	const filteredAuditoriums = (selectedBuildingId && selectedEarmarkId) 
		? auditoriums.filter(a => a.buildingId === selectedBuildingId && a.earmarkId === selectedEarmarkId) 
		: []

	const buildingColumns: GridColDef[] = [{ field: 'name', headerName: 'Корпус', flex: 1 }]
	const earmarkColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Тип', flex: 1 },
		{ field: 'size', headerName: 'Місць', width: 60 },
	]
	const auditoriumColumns: GridColDef[] = [{ field: 'name', headerName: 'Кабінет', flex: 1 }]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			{/* 1. Buildings */}
			<Grid size={4} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
				<Typography variant='subtitle2' gutterBottom sx={{ fontWeight: 'bold' }}>1. Оберіть Корпус</Typography>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={buildings}
						columns={buildingColumns}
						onRowClick={params => {
							setSelectedBuildingId(params.row.id as number)
							setSelectedEarmarkId(null)
							setSelectedAuditoriumId(null)
						}}
						density='compact'
						hideFooter
						getRowClassName={params => params.row.id === selectedBuildingId ? 'Mui-selected' : ''}
						sx={{ '& .Mui-selected': { bgcolor: 'primary.light' } }}
					/>
				</Paper>
			</Grid>

			{/* 2. Earmarks (Room Types) */}
			<Grid size={4} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
				<Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 0.5 }}>
					<Typography variant='subtitle2' sx={{ fontWeight: 'bold' }}>2. Тип (Earmark)</Typography>
					<Box>
						<IconButton size='small' disabled={!selectedBuildingId} onClick={handleAddEarmark}><AddIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedEarmarkId} onClick={handleEditEarmark}><EditIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedEarmarkId} onClick={handleDeleteEarmark}><DeleteIcon fontSize="small"/></IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1, opacity: selectedBuildingId ? 1 : 0.5 }}>
					<DataGrid
						rows={filteredEarmarks}
						columns={earmarkColumns}
						loading={isLoadingEarmarks}
						onRowClick={params => {
							setSelectedEarmarkId(params.row.id as number)
							setSelectedAuditoriumId(null)
						}}
						density='compact'
						hideFooter
						getRowClassName={params => params.row.id === selectedEarmarkId ? 'Mui-selected' : ''}
						sx={{ '& .Mui-selected': { bgcolor: 'secondary.light' } }}
					/>
				</Paper>
			</Grid>

			{/* 3. Auditoriums */}
			<Grid size={4} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
				<Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 0.5 }}>
					<Typography variant='subtitle2' sx={{ fontWeight: 'bold' }}>3. Аудиторія</Typography>
					<Box>
						<IconButton size='small' disabled={!selectedEarmarkId} onClick={handleAddAuditorium}><AddIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedAuditoriumId} onClick={handleEditAuditorium}><EditIcon fontSize="small"/></IconButton>
						<IconButton size='small' disabled={!selectedAuditoriumId} onClick={handleDeleteAuditorium}><DeleteIcon fontSize="small"/></IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1, opacity: selectedEarmarkId ? 1 : 0.5 }}>
					<DataGrid
						rows={filteredAuditoriums}
						columns={auditoriumColumns}
						loading={isLoadingAuditoriums}
						onRowClick={params => setSelectedAuditoriumId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params => params.row.id === selectedAuditoriumId ? 'Mui-selected' : ''}
					/>
				</Paper>
			</Grid>

			{/* Earmark Dialog */}
			<Dialog open={openEarmarkDialog} onClose={() => setOpenEarmarkDialog(false)}>
				<DialogTitle>{earmarkDialogMode === 'create' ? 'Додати тип' : 'Редагувати тип'}</DialogTitle>
				<DialogContent>
					<FormControl fullWidth margin="dense">
						<InputLabel>Корпус</InputLabel>
						<Select
							value={earmarkFormData.buildingId || ''}
							label="Корпус"
							onChange={e => setEarmarkFormData({ ...earmarkFormData, buildingId: e.target.value as number })}
						>
							{buildings.map(b => <MenuItem key={b.id} value={b.id}>{b.name}</MenuItem>)}
						</Select>
					</FormControl>
					<TextField
						margin='dense'
						label='Назва типу'
						fullWidth
						value={earmarkFormData.name || ''}
						onChange={e => setEarmarkFormData({ ...earmarkFormData, name: e.target.value })}
					/>
					<TextField
						margin='dense'
						label='К-ть місць'
						type="number"
						fullWidth
						value={earmarkFormData.size || ''}
						onChange={e => setEarmarkFormData({ ...earmarkFormData, size: parseInt(e.target.value) })}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenEarmarkDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmitEarmark}>Зберегти</Button>
				</DialogActions>
			</Dialog>

			{/* Auditorium Dialog */}
			<Dialog open={openAuditoriumDialog} onClose={() => setOpenAuditoriumDialog(false)}>
				<DialogTitle>{auditoriumDialogMode === 'create' ? 'Додати аудиторію' : 'Редагувати аудиторію'}</DialogTitle>
				<DialogContent>
					<FormControl fullWidth margin="dense">
						<InputLabel>Корпус</InputLabel>
						<Select
							value={auditoriumFormData.buildingId || ''}
							label="Корпус"
							disabled
						>
							{buildings.map(b => <MenuItem key={b.id} value={b.id}>{b.name}</MenuItem>)}
						</Select>
					</FormControl>
					<FormControl fullWidth margin="dense">
						<InputLabel>Тип (Earmark)</InputLabel>
						<Select
							value={auditoriumFormData.earmarkId || ''}
							label="Тип (Earmark)"
							disabled
						>
							{earmarks.map(e => <MenuItem key={e.id} value={e.id}>{e.name}</MenuItem>)}
						</Select>
					</FormControl>
					<TextField
						margin='dense'
						label='Назва (№ кабінету)'
						fullWidth
						value={auditoriumFormData.name || ''}
						onChange={e => setAuditoriumFormData({ ...auditoriumFormData, name: e.target.value })}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenAuditoriumDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmitAuditorium}>Зберегти</Button>
				</DialogActions>
			</Dialog>
		</Grid>
	)
}
