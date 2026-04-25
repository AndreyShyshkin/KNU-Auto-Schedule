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
import TeacherWishesView from '@/components/views/TeacherWishesView'
import DataExchangeView from '@/components/views/DataExchangeView'
import BuildView from '@/components/views/BuildView'
import AdminUserView from '@/components/views/AdminUserView'
import Link from 'next/link'
import {
	AppBar,
	Box,
	Button,
	Tab,
	Tabs,
	Toolbar,
	Typography,
	Dialog,
	DialogTitle,
	DialogContent,
	IconButton,
} from '@mui/material'
import OpenInNewIcon from '@mui/icons-material/OpenInNew'
import StorageIcon from '@mui/icons-material/Storage'
import CloseIcon from '@mui/icons-material/Close'
import LogoutIcon from '@mui/icons-material/Logout'
import { useState } from 'react'
import { useAuth } from '@/app/providers'
import { useRouter } from 'next/navigation'

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
	const [openDataModal, setOpenDataModal] = useState(false)
	const { logout } = useAuth()
	const router = useRouter()

	const handleLogout = () => {
		logout()
		router.push('/login')
	}

	const handleChange = (event: React.SyntheticEvent, newValue: number) => {
		setValue(newValue)
	}

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

					<Button
						startIcon={<LogoutIcon />}
						variant="outlined"
						size="small"
						color="error"
						onClick={handleLogout}
					>
						Вийти
					</Button>
				</Toolbar>
			</AppBar>

			<Box sx={{ flexGrow: 1, display: 'flex', overflow: 'hidden' }}>
				{/* Main Pane (Tabs) */}
				<Box
					sx={{
						width: '100%',
						display: 'flex',
						flexDirection: 'column',
					}}
				>
					<Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
						<Tabs
							value={value}
							onChange={handleChange}
							variant='scrollable'
							scrollButtons='auto'
						>
							<Tab label='Алгоритм' />
							<Tab label='Building' />
							<Tab label='Lesson Type' />
							<Tab label='Date' />
							<Tab label='Chair' />
							<Tab label='Speciality' />
							<Tab label='Teacher' />
							<Tab label='Побажання' />
							<Tab label='Group' />
							<Tab label='Placement' />
							<Tab label='Subject' />
							<Tab label='Lesson' />
							<Tab label='Адміни' />
						</Tabs>
					</Box>

					<Box
						sx={{ flexGrow: 1, overflow: 'auto', bgcolor: 'background.paper' }}
					>
						<TabPanel value={value} index={0}>
							<BuildView />
						</TabPanel>
						<TabPanel value={value} index={1}>
							<BuildingView />
						</TabPanel>
						<TabPanel value={value} index={2}>
							<LessonTypeView />
						</TabPanel>
						<TabPanel value={value} index={3}>
							<DateView />
						</TabPanel>
						<TabPanel value={value} index={4}>
							<ChairView />
						</TabPanel>
						<TabPanel value={value} index={5}>
							<SpecialityView />
						</TabPanel>
						<TabPanel value={value} index={6}>
							<TeacherView />
						</TabPanel>
						<TabPanel value={value} index={7}>
							<TeacherWishesView />
						</TabPanel>
						<TabPanel value={value} index={8}>
							<GroupView />
						</TabPanel>
						<TabPanel value={value} index={9}>
							<PlacementView />
						</TabPanel>
						<TabPanel value={value} index={10}>
							<SubjectView />
						</TabPanel>
						<TabPanel value={value} index={11}>
							<LessonView />
						</TabPanel>
						<TabPanel value={value} index={12}>
							<AdminUserView />
						</TabPanel>
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
