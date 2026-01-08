'use client'

import {
	createSpeciality,
	deleteSpeciality,
	fetchFaculties,
	fetchSpecialities,
	Speciality,
	updateSpeciality,
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
} from '@mui/material'
import { DataGrid, GridColDef } from '@mui/x-data-grid'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

export default function SpecialityView() {
	const queryClient = useQueryClient()
	const [selectedFacultyId, setSelectedFacultyId] = useState<number | null>(
		null
	)
	const [selectedSpecId, setSelectedSpecId] = useState<number | null>(null)

	const { data: faculties = [], isLoading: isLoadingFaculties } = useQuery({
		queryKey: ['faculties'],
		queryFn: fetchFaculties,
	})

	const { data: specialities = [], isLoading: isLoadingSpecialities } =
		useQuery({
			queryKey: ['specialities'],
			queryFn: fetchSpecialities,
		})

	const createSpecMutation = useMutation({
		mutationFn: createSpeciality,
		onSuccess: () =>
			queryClient.invalidateQueries({ queryKey: ['specialities'] }),
	})

	const updateSpecMutation = useMutation({
		mutationFn: (data: { id: number; spec: Partial<Speciality> }) =>
			updateSpeciality(data.id, data.spec),
		onSuccess: () =>
			queryClient.invalidateQueries({ queryKey: ['specialities'] }),
	})

	const deleteSpecMutation = useMutation({
		mutationFn: deleteSpeciality,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['specialities'] })
			setSelectedSpecId(null)
		},
	})

	// UI State
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Speciality>>({})

	const handleAdd = () => {
		if (!selectedFacultyId) return
		setFormData({ facultyId: selectedFacultyId })
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const spec = specialities.find(s => s.id === selectedSpecId)
		if (spec) {
			setFormData(spec)
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedSpecId && confirm('Delete this speciality?')) {
			deleteSpecMutation.mutate(selectedSpecId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createSpecMutation.mutate(formData)
		} else if (selectedSpecId) {
			updateSpecMutation.mutate({ id: selectedSpecId, spec: formData })
		}
		setOpenDialog(false)
	}

	const filteredSpecialities = selectedFacultyId
		? specialities.filter(s => s.facultyId === selectedFacultyId)
		: []

	const facultyColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Faculty Name', flex: 1 },
		{ field: 'description', headerName: 'Description', flex: 1 },
	]

	const specialityColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Speciality Name', flex: 1 },
		{ field: 'description', headerName: 'Description', flex: 1 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			<Grid
				item
				xs={6}
				sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
			>
				<Typography variant='subtitle1' gutterBottom>
					Faculties
				</Typography>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={faculties}
						columns={facultyColumns}
						loading={isLoadingFaculties}
						onRowClick={params => setSelectedFacultyId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedFacultyId ? 'Mui-selected' : ''
						}
						sx={{
							'& .Mui-selected': {
								bgcolor: 'primary.light',
								'&:hover': { bgcolor: 'primary.light' },
							},
						}}
					/>
				</Paper>
			</Grid>

			<Grid
				item
				xs={6}
				sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
			>
				<Box
					sx={{
						display: 'flex',
						justifyContent: 'space-between',
						alignItems: 'center',
						mb: 1,
					}}
				>
					<Typography variant='subtitle1'>
						{selectedFacultyId ? 'Specialities' : 'Select a Faculty'}
					</Typography>
					<Box>
						<IconButton
							size='small'
							disabled={!selectedFacultyId}
							onClick={handleAdd}
						>
							<AddIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedSpecId}
							onClick={handleEdit}
						>
							<EditIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedSpecId}
							onClick={handleDelete}
						>
							<DeleteIcon />
						</IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={filteredSpecialities}
						columns={specialityColumns}
						loading={isLoadingSpecialities}
						onRowClick={params => setSelectedSpecId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedSpecId ? 'Mui-selected' : ''
						}
						sx={{
							'& .Mui-selected': {
								bgcolor: 'primary.light',
								'&:hover': { bgcolor: 'primary.light' },
							},
						}}
					/>
				</Paper>
			</Grid>

			<Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
				<DialogTitle>
					{dialogMode === 'create' ? 'Add Speciality' : 'Edit Speciality'}
				</DialogTitle>
				<DialogContent>
					<TextField
						autoFocus
						margin='dense'
						label='Name'
						fullWidth
						value={formData.name || ''}
						onChange={e => setFormData({ ...formData, name: e.target.value })}
					/>
					<TextField
						margin='dense'
						label='Description'
						fullWidth
						value={formData.description || ''}
						onChange={e =>
							setFormData({ ...formData, description: e.target.value })
						}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDialog(false)}>Cancel</Button>
					<Button onClick={handleSubmit}>Save</Button>
				</DialogActions>
			</Dialog>
		</Grid>
	)
}
