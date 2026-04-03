'use client'

import {
	LessonType,
	createLessonType,
	deleteLessonType,
	fetchLessonTypes,
	updateLessonType,
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

export default function LessonTypeView() {
	const queryClient = useQueryClient()
	const [selectedId, setSelectedId] = useState<number | null>(null)
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<LessonType>>({})

	const { data: types = [], isLoading } = useQuery({
		queryKey: ['lessonTypes'],
		queryFn: fetchLessonTypes,
	})

	const createMutation = useMutation({
		mutationFn: createLessonType,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['lessonTypes'] })
			setOpenDialog(false)
		},
	})

	const updateMutation = useMutation({
		mutationFn: (data: { id: number; type: Partial<LessonType> }) =>
			updateLessonType(data.id, data.type),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['lessonTypes'] })
			setOpenDialog(false)
		},
	})

	const deleteMutation = useMutation({
		mutationFn: deleteLessonType,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['lessonTypes'] })
			setSelectedId(null)
		},
	})

	const handleAdd = () => {
		setFormData({})
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const type = types.find(t => t.id === selectedId)
		if (type) {
			setFormData(type)
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedId && confirm('Видалити цей тип заняття?')) {
			deleteMutation.mutate(selectedId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createMutation.mutate(formData)
		} else if (selectedId) {
			updateMutation.mutate({ id: selectedId, type: formData })
		}
	}

	const columns: GridColDef[] = [
		{ field: 'id', headerName: 'ID', width: 90 },
		{ field: 'name', headerName: 'Тип заняття (напр. Лекція)', flex: 1 },
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
					rows={types}
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
					{dialogMode === 'create' ? 'Додати тип заняття' : 'Редагувати тип заняття'}
				</DialogTitle>
				<DialogContent>
					<TextField
						autoFocus
						margin='dense'
						label='Назва (Лекція, Практика...)'
						fullWidth
						value={formData.name || ''}
						onChange={e => setFormData({ ...formData, name: e.target.value })}
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
