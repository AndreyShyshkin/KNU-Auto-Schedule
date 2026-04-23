'use client'

import {
	User,
	createAdminUser,
	deleteAdminUser,
	fetchAdminUsers,
	handleError,
} from '@/lib/api/scheduleApi'
import {
	Add as AddIcon,
	Delete as DeleteIcon,
} from '@mui/icons-material'
import {
	Box,
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogTitle,
	Paper,
	TextField,
	Typography,
} from '@mui/material'
import { DataGrid, GridColDef } from '@mui/x-data-grid'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'

export default function AdminUserView() {
	const queryClient = useQueryClient()
	const [selectedId, setSelectedId] = useState<number | null>(null)
	const [openDialog, setOpenDialog] = useState(false)
	const [formData, setFormData] = useState<Partial<User>>({ role: 'ADMIN' })

	const { data: users = [], isLoading } = useQuery({
		queryKey: ['admin-users'],
		queryFn: fetchAdminUsers,
	})

	const createMutation = useMutation({
		mutationFn: createAdminUser,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['admin-users'] })
			setOpenDialog(false)
			setFormData({ role: 'ADMIN' })
		},
		onError: (error) => alert(handleError(error)),
	})

	const deleteMutation = useMutation({
		mutationFn: deleteAdminUser,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['admin-users'] })
			setSelectedId(null)
		},
		onError: (error) => alert(handleError(error)),
	})

	const handleAdd = () => {
		setFormData({ role: 'ADMIN' })
		setOpenDialog(true)
	}

	const handleDelete = () => {
		if (selectedId && confirm('Delete this admin user?')) {
			deleteMutation.mutate(selectedId)
		}
	}

	const handleSubmit = () => {
		if (formData.username && formData.password) {
			createMutation.mutate(formData as User)
		} else {
			alert('Please provide username and password')
		}
	}

	const columns: GridColDef[] = [
		{ field: 'id', headerName: 'ID', width: 90 },
		{ field: 'username', headerName: 'Username', flex: 1 },
		{ field: 'role', headerName: 'Role', flex: 1 },
	]

	return (
		<Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Typography variant="h6" sx={{ mb: 2 }}>Керування адміністраторами</Typography>
			<Box sx={{ mb: 2, display: 'flex', gap: 1 }}>
				<Button variant='contained' startIcon={<AddIcon />} onClick={handleAdd}>
					Додати адміністратора
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
					rows={users}
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
				<DialogTitle>Додати нового адміністратора</DialogTitle>
				<DialogContent>
					<TextField
						autoFocus
						margin='dense'
						label='Username'
						fullWidth
						value={formData.username || ''}
						onChange={e => setFormData({ ...formData, username: e.target.value })}
					/>
					<TextField
						margin='dense'
						label='Password'
						type="password"
						fullWidth
						value={formData.password || ''}
						onChange={e =>
							setFormData({ ...formData, password: e.target.value })
						}
					/>
				</DialogContent>
				<DialogActions>
					<Button onClick={() => setOpenDialog(false)}>Скасувати</Button>
					<Button onClick={handleSubmit}>Створити</Button>
				</DialogActions>
			</Dialog>
		</Box>
	)
}
