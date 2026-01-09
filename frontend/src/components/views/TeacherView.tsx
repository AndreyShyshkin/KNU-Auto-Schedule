'use client'

import {
	createTeacher,
	deleteTeacher,
	fetchChairs,
	fetchTeachers,
	Teacher,
	updateTeacher,
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

export default function TeacherView() {
	const queryClient = useQueryClient()
	const [selectedChairId, setSelectedChairId] = useState<number | null>(null)
	const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(
		null
	)

	const { data: chairs = [], isLoading: isLoadingChairs } = useQuery({
		queryKey: ['chairs'],
		queryFn: fetchChairs,
	})

	const { data: teachers = [], isLoading: isLoadingTeachers } = useQuery({
		queryKey: ['teachers'],
		queryFn: fetchTeachers,
	})

	const createTeacherMutation = useMutation({
		mutationFn: createTeacher,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['teachers'] }),
	})

	const updateTeacherMutation = useMutation({
		mutationFn: (data: { id: number; teacher: Partial<Teacher> }) =>
			updateTeacher(data.id, data.teacher),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['teachers'] }),
	})

	const deleteTeacherMutation = useMutation({
		mutationFn: deleteTeacher,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['teachers'] })
			setSelectedTeacherId(null)
		},
	})

	// UI State
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Teacher>>({})

	const handleAdd = () => {
		if (!selectedChairId) return
		setFormData({ departmentId: selectedChairId })
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const teacher = teachers.find(t => t.id === selectedTeacherId)
		if (teacher) {
			setFormData(teacher)
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedTeacherId && confirm('Delete this teacher?')) {
			deleteTeacherMutation.mutate(selectedTeacherId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createTeacherMutation.mutate(formData)
		} else if (selectedTeacherId) {
			updateTeacherMutation.mutate({ id: selectedTeacherId, teacher: formData })
		}
		setOpenDialog(false)
	}

	const filteredTeachers = selectedChairId
		? teachers.filter(t => t.departmentId === selectedChairId)
		: []

	const chairColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Chair Name', flex: 1 },
		{ field: 'facultyName', headerName: 'Faculty', flex: 1 },
	]

	const teacherColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Teacher Name', flex: 1 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			<Grid
				size={6}
				sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
			>
				<Typography variant='subtitle1' gutterBottom>
					Chairs
				</Typography>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={chairs}
						columns={chairColumns}
						loading={isLoadingChairs}
						onRowClick={params => setSelectedChairId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedChairId ? 'Mui-selected' : ''
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
				size={6}
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
						{selectedChairId ? 'Teachers' : 'Select a Chair'}
					</Typography>
					<Box>
						<IconButton
							size='small'
							disabled={!selectedChairId}
							onClick={handleAdd}
						>
							<AddIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedTeacherId}
							onClick={handleEdit}
						>
							<EditIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedTeacherId}
							onClick={handleDelete}
						>
							<DeleteIcon />
						</IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={filteredTeachers}
						columns={teacherColumns}
						loading={isLoadingTeachers}
						onRowClick={params => setSelectedTeacherId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedTeacherId ? 'Mui-selected' : ''
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
					{dialogMode === 'create' ? 'Add Teacher' : 'Edit Teacher'}
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
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDialog(false)}>Cancel</Button>
					<Button onClick={handleSubmit}>Save</Button>
				</DialogActions>
			</Dialog>
		</Grid>
	)
}
