'use client'

import {
	Building,
	createBuilding,
	deleteBuilding,
	fetchBuildings,
	updateBuilding,
	handleError,
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
	IconButton,
	Paper,
	TextField,
} from '@mui/material'
import { DataGrid, GridColDef } from '@mui/x-data-grid'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

export default function BuildingView() {
	const queryClient = useQueryClient()
	const [selectedId, setSelectedId] = useState<number | null>(null)
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Building>>({})

	const { data: buildings = [], isLoading } = useQuery({
		queryKey: ['buildings'],
		queryFn: fetchBuildings,
	})

	const createMutation = useMutation({
		mutationFn: createBuilding,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['buildings'] })
			setOpenDialog(false)
		},
		onError: (error) => alert(handleError(error)),
	})

	const updateMutation = useMutation({
		mutationFn: (data: { id: number; building: Partial<Building> }) =>
			updateBuilding(data.id, data.building),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['buildings'] })
			setOpenDialog(false)
		},
		onError: (error) => alert(handleError(error)),
	})

	const deleteMutation = useMutation({
		mutationFn: deleteBuilding,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['buildings'] })
			setSelectedId(null)
		},
		onError: (error) => alert(handleError(error)),
	})

	const handleAdd = () => {
		setFormData({})
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const building = buildings.find(b => b.id === selectedId)
		if (building) {
			setFormData(building)
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedId && confirm('Delete this building?')) {
			deleteMutation.mutate(selectedId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createMutation.mutate(formData)
		} else if (selectedId) {
			updateMutation.mutate({ id: selectedId, building: formData })
		}
	}

	const columns: GridColDef[] = [
		{ field: 'id', headerName: 'ID', width: 90 },
		{ field: 'name', headerName: 'Назва', flex: 1 },
		{ field: 'description', headerName: 'Опис', flex: 2 },
	]

	return (
		<Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
			<Box sx={{ mb: 2, display: 'flex', gap: 1 }}>
				<Button variant='contained' startIcon={<AddIcon />} onClick={handleAdd}>
					Додати
				</Button>
				<Button
					variant='outlined'
					startIcon={<EditIcon />}
					disabled={!selectedId}
					onClick={handleEdit}
				>
					Редагувати
				</Button>
				<Button
					variant='outlined'
					color='error'
					startIcon={<DeleteIcon />}
					disabled={!selectedId}
					onClick={handleDelete}
				>
					Видалити
				</Button>
			</Box>

			<Paper sx={{ flexGrow: 1 }}>
				<DataGrid
					rows={buildings}
					columns={columns}
					loading={isLoading}
					onRowClick={params => setSelectedId(params.row.id as number)}
					density='compact'
					hideFooter
					getRowClassName={params =>
						params.row.id === selectedId ? 'Mui-selected' : ''
					}
					sx={{
						'& .Mui-selected': {
							bgcolor: 'primary.light',
							'&:hover': { bgcolor: 'primary.light' },
						},
					}}
				/>
			</Paper>

			<Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
				<DialogTitle>
					{dialogMode === 'create' ? 'Додати корпус' : 'Редагувати корпус'}
				</DialogTitle>
				<DialogContent>
					<TextField
						autoFocus
						margin='dense'
						label='Назва'
						fullWidth
						value={formData.name || ''}
						onChange={e => setFormData({ ...formData, name: e.target.value })}
					/>
					<TextField
						margin='dense'
						label='Опис'
						fullWidth
						value={formData.description || ''}
						onChange={e =>
							setFormData({ ...formData, description: e.target.value })
						}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmit}>Зберегти</Button>
				</DialogActions>
			</Dialog>
		</Box>
	)
}
