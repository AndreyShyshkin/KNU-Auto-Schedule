'use client'

import {
	getBuildStatus,
	startBuild,
	fetchScheduleVersions,
	deleteScheduleVersion,
	ScheduleVersion,
	BuildStatus,
	handleError,
} from '@/lib/api/scheduleApi'
import {
	Box,
	Button,
	CircularProgress,
	Divider,
	List,
	ListItem,
	ListItemText,
	Paper,
	Typography,
	IconButton,
	Alert,
	TextField,
} from '@mui/material'
import {
	Delete as DeleteIcon,
	PlayArrow as PlayIcon,
	OpenInNew as OpenInNewIcon,
} from '@mui/icons-material'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import Link from 'next/link'

export default function BuildView() {
	const queryClient = useQueryClient()
	const [isBuilding, setIsBuilding] = useState(false)
	const [lastStatus, setLastStatus] = useState<BuildStatus | null>(null)
	
	const [startDate, setStartDate] = useState('')
	const [endDate, setEndDate] = useState('')

	const { data: versions = [], isLoading: isLoadingVersions } = useQuery({
		queryKey: ['scheduleVersions'],
		queryFn: fetchScheduleVersions,
	})

	const handleBuild = async () => {
		try {
			await startBuild(startDate || undefined, endDate || undefined)
			setIsBuilding(true)
			setLastStatus(null)
		} catch (error) {
			alert(handleError(error))
		}
	}

	const deleteMutation = useMutation({
		mutationFn: (id?: number) => deleteScheduleVersion(id),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ['scheduleVersions'] })
		},
		onError: (error) => alert(handleError(error)),
	})

	const handleDeleteVersion = (id: number) => {
		if (confirm('Видалити цю версію розкладу?')) {
			deleteMutation.mutate(id)
		}
	}

	useEffect(() => {
		let interval: NodeJS.Timeout
		if (isBuilding) {
			interval = setInterval(async () => {
				try {
					const status = await getBuildStatus()
					setIsBuilding(status.building)
					setLastStatus(status)
					if (!status.building) {
						queryClient.invalidateQueries({ queryKey: ['scheduleVersions'] })
					}
				} catch (e) {
					console.error(e)
				}
			}, 1000)
		}
		return () => clearInterval(interval)
	}, [isBuilding, queryClient])

	return (
		<Box sx={{ display: 'flex', gap: 3, height: '100%' }}>
			{/* Execution Panel */}
			<Paper sx={{ p: 3, flex: '1 1 40%', display: 'flex', flexDirection: 'column', gap: 2 }} elevation={2}>
				<Typography variant='h6' color='primary'>Алгоритм побудови</Typography>
				<Typography variant='body2' color='text.secondary'>
					Вкажіть період семестру та натисніть кнопку нижче, щоб запустити генерацію. 
					Система розподілить загальну кількість годин кожного заняття по календарних датах.
				</Typography>

				<Box sx={{ display: 'flex', gap: 2, mt: 1 }}>
					<TextField
						label='Дата початку'
						type='date'
						fullWidth
						size='small'
						InputLabelProps={{ shrink: true }}
						value={startDate}
						onChange={(e) => setStartDate(e.target.value)}
						disabled={isBuilding}
					/>
					<TextField
						label='Дата закінчення'
						type='date'
						fullWidth
						size='small'
						InputLabelProps={{ shrink: true }}
						value={endDate}
						onChange={(e) => setEndDate(e.target.value)}
						disabled={isBuilding}
					/>
				</Box>

				<Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', gap: 2 }}>
					{isBuilding ? (
						<Box sx={{ textAlign: 'center' }}>
							<CircularProgress size={60} sx={{ mb: 2 }} />
							<Typography variant="h5">Крок {lastStatus?.steps || 0}</Typography>
							<Typography variant="body2">Триває генерація...</Typography>
						</Box>
					) : (
						<Button
							variant="contained"
							size="large"
							startIcon={<PlayIcon />}
							onClick={handleBuild}
							sx={{ px: 4, py: 2, borderRadius: 10 }}
							disabled={!startDate || !endDate}
						>
							Почати генерацію
						</Button>
					)}
				</Box>

				{lastStatus && !isBuilding && (
					<Alert 
						severity={lastStatus.lastResult === 'DONE' ? "success" : "error"}
						sx={{ mt: 2 }}
					>
						<Box sx={{ whiteSpace: 'pre-line' }}>
							{lastStatus.lastResult === 'DONE' 
								? `Розклад успішно згенеровано за ${lastStatus.steps} кроків!`
								: `Помилка: ${lastStatus.lastError || 'Не вдалося знайти рішення'}`}
						</Box>
					</Alert>
				)}
			</Paper>

			{/* History Panel */}
			<Paper sx={{ p: 3, flex: '1 1 60%', display: 'flex', flexDirection: 'column' }} elevation={2}>
				<Typography variant='h6' gutterBottom>Історія версій</Typography>
				<Divider sx={{ mb: 2 }} />
				
				<List sx={{ flexGrow: 1, overflow: 'auto' }}>
					{versions.length === 0 && !isLoadingVersions && (
						<Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', mt: 4 }}>
							Немає збережених версій розкладу.
						</Typography>
					)}
					{versions.map((v) => (
						<ListItem
							key={v.id}
							divider
							secondaryAction={
								<Box sx={{ display: 'flex', gap: 1 }}>
									<Button 
										size="small" 
										component={Link} 
										href="/results"
										startIcon={<OpenInNewIcon />}
									>
										Переглянути
									</Button>
									<IconButton 
										edge="end" 
										color="error" 
										size="small"
										onClick={() => handleDeleteVersion(v.id)}
									>
										<DeleteIcon />
									</IconButton>
								</Box>
							}
						>
							<ListItemText
								primary={v.name}
								secondary={
									<Box>
										<Typography variant="caption" display="block">
											{v.current ? "Поточна активна версія" : `Створено: ${new Date(v.createdAt).toLocaleString()}`}
										</Typography>
										{v.startDate && v.endDate && (
											<Typography variant="caption" color="primary">
												Період: {v.startDate} — {v.endDate}
											</Typography>
										)}
									</Box>
								}
								primaryTypographyProps={{ fontWeight: v.current ? 'bold' : 'normal' }}
							/>
						</ListItem>
					))}
				</List>
				
				<Button 
					variant="outlined" 
					color="error" 
					size="small" 
					startIcon={<DeleteIcon />}
					sx={{ mt: 2, alignSelf: 'flex-start' }}
					onClick={() => {
						if (confirm('Видалити абсолютно ВСІ версії розкладу?')) {
							deleteMutation.mutate(undefined)
						}
					}}
				>
					Очистити всю історію
				</Button>
			</Paper>
		</Box>
	)
}
