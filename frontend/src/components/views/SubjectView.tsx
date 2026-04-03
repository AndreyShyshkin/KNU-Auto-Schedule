'use client'

import {
	createSubject,
	deleteSubject,
	fetchSubjects,
	fetchFaculties,
	handleError,
	Subject,
	updateSubject,
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

export default function SubjectView() {
	const queryClient = useQueryClient()
	const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(
		null
	)

	const { data: faculties = [] } = useQuery({
		queryKey: ['faculties'],
		queryFn: fetchFaculties,
	})

	const { data: subjects = [], isLoading } = useQuery({
		queryKey: ['subjects'],
		queryFn: fetchSubjects,
	})

	const createSubjectMutation = useMutation({
		mutationFn: createSubject,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['subjects'] }),
		onError: (error) => alert(handleError(error)),
	})

	const updateSubjectMutation = useMutation({
		mutationFn: (data: { id: number; subject: Partial<Subject> }) =>
			updateSubject(data.id, data.subject),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['subjects'] }),
		onError: (error) => alert(handleError(error)),
	})

	const deleteSubjectMutation = useMutation({
		mutationFn: deleteSubject,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['subjects'] })
			setSelectedSubjectId(null)
		},
		onError: (error) => alert(handleError(error)),
	})

	// UI State
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Subject>>({})

	const handleAdd = () => {
		setFormData({})
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const subject = subjects.find(s => s.id === selectedSubjectId)
		if (subject) {
			setFormData(subject)
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedSubjectId && confirm('Delete this subject?')) {
			deleteSubjectMutation.mutate(selectedSubjectId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createSubjectMutation.mutate(formData)
		} else if (selectedSubjectId) {
			updateSubjectMutation.mutate({ id: selectedSubjectId, subject: formData })
		}
		setOpenDialog(false)
	}

	const columns: GridColDef[] = [
		{ field: 'name', headerName: 'Назва предмета', flex: 1 },
		{ field: 'facultyName', headerName: 'Факультет', width: 200 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			<Grid
				size={12}
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
					<Typography variant='subtitle1'>Предмети</Typography>
					<Box>
						<IconButton size='small' onClick={handleAdd}>
							<AddIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedSubjectId}
							onClick={handleEdit}
						>
							<EditIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedSubjectId}
							onClick={handleDelete}
						>
							<DeleteIcon />
						</IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={subjects}
						columns={columns}
						loading={isLoading}
						onRowClick={params => setSelectedSubjectId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedSubjectId ? 'Mui-selected' : ''
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
					{dialogMode === 'create' ? 'Додати предмет' : 'Редагувати предмет'}
				</DialogTitle>
				<DialogContent>
					<FormControl fullWidth margin="dense">
						<InputLabel>Факультет</InputLabel>
						<Select
							value={formData.facultyId || ''}
							label="Факультет"
							onChange={e =>
								setFormData({
									...formData,
									facultyId: e.target.value as number,
								})
							}
						>
							{faculties.map(f => (
								<MenuItem key={f.id} value={f.id}>
									{f.name}
								</MenuItem>
							))}
						</Select>
					</FormControl>
					<TextField
						margin='dense'
						label='Назва'
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
		</Grid>
	)
}
