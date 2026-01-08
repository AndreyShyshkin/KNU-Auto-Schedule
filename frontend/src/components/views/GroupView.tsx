'use client'

import {
	createGroup,
	deleteGroup,
	fetchGroups,
	fetchSpecialities,
	Group,
	updateGroup,
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

export default function GroupView() {
	const queryClient = useQueryClient()
	const [selectedSpecId, setSelectedSpecId] = useState<number | null>(null)
	const [selectedGroupId, setSelectedGroupId] = useState<number | null>(null)

	const { data: specialities = [], isLoading: isLoadingSpecs } = useQuery({
		queryKey: ['specialities'],
		queryFn: fetchSpecialities,
	})

	const { data: groups = [], isLoading: isLoadingGroups } = useQuery({
		queryKey: ['groups'],
		queryFn: fetchGroups,
	})

	const createGroupMutation = useMutation({
		mutationFn: createGroup,
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['groups'] }),
	})

	const updateGroupMutation = useMutation({
		mutationFn: (data: { id: number; group: Partial<Group> }) =>
			updateGroup(data.id, data.group),
		onSuccess: () => queryClient.invalidateQueries({ queryKey: ['groups'] }),
	})

	const deleteGroupMutation = useMutation({
		mutationFn: deleteGroup,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['groups'] })
			setSelectedGroupId(null)
		},
	})

	// UI State
	const [openDialog, setOpenDialog] = useState(false)
	const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create')
	const [formData, setFormData] = useState<Partial<Group>>({})

	const handleAdd = () => {
		if (!selectedSpecId) return
		setFormData({ departmentId: selectedSpecId, year: 1, size: 20 })
		setDialogMode('create')
		setOpenDialog(true)
	}

	const handleEdit = () => {
		const group = groups.find(g => g.id === selectedGroupId)
		if (group) {
			setFormData(group)
			setDialogMode('edit')
			setOpenDialog(true)
		}
	}

	const handleDelete = () => {
		if (selectedGroupId && confirm('Delete this group?')) {
			deleteGroupMutation.mutate(selectedGroupId)
		}
	}

	const handleSubmit = () => {
		if (dialogMode === 'create') {
			createGroupMutation.mutate(formData)
		} else if (selectedGroupId) {
			updateGroupMutation.mutate({ id: selectedGroupId, group: formData })
		}
		setOpenDialog(false)
	}

	const filteredGroups = selectedSpecId
		? groups.filter(g => g.departmentId === selectedSpecId)
		: []

	const specColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Speciality Name', flex: 1 },
		{ field: 'facultyName', headerName: 'Faculty', flex: 1 },
	]

	const groupColumns: GridColDef[] = [
		{ field: 'name', headerName: 'Group Name', flex: 1 },
		{ field: 'year', headerName: 'Year', width: 70 },
		{ field: 'size', headerName: 'Size', width: 70 },
	]

	return (
		<Grid container spacing={2} sx={{ height: '100%' }}>
			<Grid
				item
				xs={6}
				sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
			>
				<Typography variant='subtitle1' gutterBottom>
					Specialities
				</Typography>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={specialities}
						columns={specColumns}
						loading={isLoadingSpecs}
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
						{selectedSpecId ? 'Groups' : 'Select a Speciality'}
					</Typography>
					<Box>
						<IconButton
							size='small'
							disabled={!selectedSpecId}
							onClick={handleAdd}
						>
							<AddIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedGroupId}
							onClick={handleEdit}
						>
							<EditIcon />
						</IconButton>
						<IconButton
							size='small'
							disabled={!selectedGroupId}
							onClick={handleDelete}
						>
							<DeleteIcon />
						</IconButton>
					</Box>
				</Box>
				<Paper sx={{ flexGrow: 1 }}>
					<DataGrid
						rows={filteredGroups}
						columns={groupColumns}
						loading={isLoadingGroups}
						onRowClick={params => setSelectedGroupId(params.row.id as number)}
						density='compact'
						hideFooter
						getRowClassName={params =>
							params.row.id === selectedGroupId ? 'Mui-selected' : ''
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
					{dialogMode === 'create' ? 'Add Group' : 'Edit Group'}
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
						label='Year'
						type='number'
						fullWidth
						value={formData.year || ''}
						onChange={e =>
							setFormData({ ...formData, year: parseInt(e.target.value) })
						}
					/>
					<TextField
						margin='dense'
						label='Size'
						type='number'
						fullWidth
						value={formData.size || ''}
						onChange={e =>
							setFormData({ ...formData, size: parseInt(e.target.value) })
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
