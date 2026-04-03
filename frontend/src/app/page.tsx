'use client'

import BuildingView from '@/components/views/BuildingView'
import LessonTypeView from '@/components/views/LessonTypeView'
import ChairView from '@/components/views/ChairView'
import DateView from '@/components/views/DateView'
import GroupView from '@/components/views/GroupView'
import LessonView from '@/components/views/LessonView'
import PlacementView from '@/components/views/PlacementView'
import SpecialityView from '@/components/views/SpecialityView'
import SubjectView from '@/components/views/SubjectView'
import TeacherView from '@/components/views/TeacherView'
import DataExchangeView from '@/components/views/DataExchangeView'
import { getBuildStatus, startBuild } from '@/lib/api/scheduleApi'
import Link from 'next/link'
import {
	AppBar,
	Box,
	Button,
	CircularProgress,
	Tab,
	Tabs,
	Toolbar,
	Typography,
	Divider,
	Dialog,
	DialogTitle,
	DialogContent,
	IconButton,
} from '@mui/material'
import OpenInNewIcon from '@mui/icons-material/OpenInNew'
import StorageIcon from '@mui/icons-material/Storage'
import CloseIcon from '@mui/icons-material/Close'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'

interface TabPanelProps {
	children?: React.ReactNode
	index: number
	value: number
}

function TabPanel(props: TabPanelProps) {
	const { children, value, index, ...other } = props

	return (
		<div
			role='tabpanel'
			hidden={value !== index}
			id={`simple-tabpanel-${index}`}
			aria-labelledby={`simple-tab-${index}`}
			{...other}
			style={{ height: '100%', overflow: 'auto' }}
		>
			{value === index && <Box sx={{ p: 2, height: '100%' }}>{children}</Box>}
		</div>
	)
}

export default function Home() {
	const [value, setValue] = useState(0)
	const [isBuilding, setIsBuilding] = useState(false)
	const [lastStatus, setLastStatus] = useState<any>(null)
	const [openDataModal, setOpenDataModal] = useState(false)
	const queryClient = useQueryClient()

	const handleChange = (event: React.SyntheticEvent, newValue: number) => {
		setValue(newValue)
	}

	const handleBuild = async () => {
		try {
			await startBuild()
			setIsBuilding(true)
			setLastStatus(null)
		} catch (error) {
			console.error('Failed to start build', error)
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
						if (status.lastResult === 'DONE') {
							alert('Build finished successfully!')
							queryClient.invalidateQueries({ queryKey: ['scheduleVersions'] })
						}
					}
				} catch (e) {
					console.error(e)
				}
			}, 1000)
		}
		return () => clearInterval(interval)
	}, [isBuilding, queryClient])

	return (
		<Box
			sx={{
				flexGrow: 1,
				height: '100vh',
				display: 'flex',
				flexDirection: 'column',
			}}
		>
			<AppBar position='static' color='default' elevation={1}>
				<Toolbar variant='dense' sx={{ gap: 1 }}>
					<Typography
						variant='h6'
						color='inherit'
						component='div'
						sx={{ flexGrow: 1 }}
					>
						KNU Schedule - Управління
					</Typography>

					<Button
						startIcon={<StorageIcon />}
						variant="outlined"
						size="small"
						onClick={() => setOpenDataModal(true)}
					>
						Імпорт/Експорт
					</Button>

					<Button
						component={Link}
						href="/results"
						startIcon={<OpenInNewIcon />}
						variant="contained"
						size="small"
						color="primary"
					>
						Переглянути розклад
					</Button>
				</Toolbar>
			</AppBar>

			<Box sx={{ flexGrow: 1, display: 'flex', overflow: 'hidden' }}>
				{/* Left Side: Input Pane (Tabs) */}
				<Box
					sx={{
						width: '75%',
						display: 'flex',
						flexDirection: 'column',
						borderRight: 1,
						borderColor: 'divider',
					}}
				>
					<Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
						<Tabs
							value={value}
							onChange={handleChange}
							variant='scrollable'
							scrollButtons='auto'
						>
							<Tab label='Building' />
							<Tab label='Lesson Type' />
							<Tab label='Date' />
							<Tab label='Chair' />
							<Tab label='Speciality' />
							<Tab label='Teacher' />
							<Tab label='Group' />
							<Tab label='Placement' />
							<Tab label='Subject' />
							<Tab label='Lesson' />
						</Tabs>
					</Box>

					<Box
						sx={{ flexGrow: 1, overflow: 'auto', bgcolor: 'background.paper' }}
					>
						<TabPanel value={value} index={0}>
							<BuildingView />
						</TabPanel>
						<TabPanel value={value} index={1}>
							<LessonTypeView />
						</TabPanel>
						<TabPanel value={value} index={2}>
							<DateView />
						</TabPanel>
						<TabPanel value={value} index={3}>
							<ChairView />
						</TabPanel>
						<TabPanel value={value} index={4}>
							<SpecialityView />
						</TabPanel>
						<TabPanel value={value} index={5}>
							<TeacherView />
						</TabPanel>
						<TabPanel value={value} index={6}>
							<GroupView />
						</TabPanel>
						<TabPanel value={value} index={7}>
							<PlacementView />
						</TabPanel>
						<TabPanel value={value} index={8}>
							<SubjectView />
						</TabPanel>
						<TabPanel value={value} index={9}>
							<LessonView />
						</TabPanel>
					</Box>
				</Box>

				{/* Right Side: Build Pane / Status */}
				<Box
					sx={{
						width: '25%',
						display: 'flex',
						flexDirection: 'column',
						borderLeft: 1,
						borderColor: 'divider',
						bgcolor: '#fafafa'
					}}
				>
					<Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
						<Typography variant='h6'>Побудова розкладу</Typography>
					</Box>

					<Box sx={{ p: 2, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
						<Typography
							variant='body2'
							color='text.secondary'
							paragraph
							align='center'
						>
							Натисніть кнопку нижче для запуску алгоритму автоматичної побудови.
						</Typography>

						{lastStatus?.lastResult && lastStatus.lastResult !== 'DONE' && (
							<Box sx={{ width: '100%', mb: 2, p: 1, bgcolor: '#fff0f0', border: '1px solid #ffcccc', borderRadius: 1 }}>
								<Typography variant="subtitle2" color="error" gutterBottom align="center">
									Помилка побудови ({lastStatus.lastResult})
								</Typography>
								<Typography variant="caption" color="error" component="div" align="center">
									{lastStatus.lastError}
								</Typography>
							</Box>
						)}

						{lastStatus?.lastResult === 'DONE' && (
							<Box sx={{ width: '100%', mb: 2, p: 1, bgcolor: '#f0fff0', border: '1px solid #ccffcc', borderRadius: 1 }}>
								<Typography variant="subtitle2" color="success.main" gutterBottom align="center">
									Розклад згенеровано за {lastStatus.steps} кроків!
								</Typography>
								<Button
									component={Link}
									href="/results"
									variant="outlined"
									color="success"
									fullWidth
									size="small"
									sx={{ mt: 1 }}
								>
									Переглянути результат
								</Button>
							</Box>
						)}

						<Button
							variant='contained'
							onClick={handleBuild}
							disabled={isBuilding}
							fullWidth
							sx={{ mt: 2 }}
						>
							{isBuilding ? `Побудова (Крок ${lastStatus?.steps || 0})...` : 'Почати генерацію'}
						</Button>

						<Button
							variant='outlined'
							color='error'
							onClick={async () => {
								if (confirm('Очистити всі результати?')) {
									await axios.delete('/api/schedule/clear')
									alert('Результати очищено')
									setLastStatus(null)
								}
							}}
							disabled={isBuilding}
							fullWidth
							sx={{ mt: 1 }}
						>
							Очистити результати
						</Button>

						{isBuilding && <CircularProgress sx={{ mt: 2 }} />}
						
						<Divider sx={{ my: 3, width: '100%' }} />
						
						<Button
							component={Link}
							href="/results"
							variant="text"
							startIcon={<OpenInNewIcon />}
						>
							Повний розклад
						</Button>
					</Box>
				</Box>
			</Box>

			{/* Data Exchange Modal */}
			<Dialog 
				open={openDataModal} 
				onClose={() => setOpenDataModal(false)}
				maxWidth="md"
				fullWidth
			>
				<DialogTitle sx={{ m: 0, p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
					Імпорт та Експорт даних
					<IconButton onClick={() => setOpenDataModal(false)}>
						<CloseIcon />
					</IconButton>
				</DialogTitle>
				<DialogContent dividers>
					<DataExchangeView />
				</DialogContent>
			</Dialog>
		</Box>
	)
}
