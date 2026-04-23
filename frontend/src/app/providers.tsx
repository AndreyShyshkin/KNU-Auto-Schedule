'use client'

import { createTheme, CssBaseline, ThemeProvider } from '@mui/material'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import React, { createContext, useContext, useEffect, useState } from 'react'
import axios from 'axios'

const theme = createTheme({
	palette: {
		mode: 'light',
		primary: {
			main: '#1976d2',
		},
		background: {
			default: '#f5f5f5',
		},
	},
})

interface AuthContextType {
	isAuthenticated: boolean
	role: string | null
	login: (username: string, password: string) => Promise<void>
	logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
	const context = useContext(AuthContext)
	if (!context) {
		throw new Error('useAuth must be used within an AuthProvider')
	}
	return context
}

export default function Providers({ children }: { children: React.ReactNode }) {
	const [queryClient] = useState(() => new QueryClient())
	const [isAuthenticated, setIsAuthenticated] = useState(false)
	const [role, setRole] = useState<string | null>(null)

	useEffect(() => {
		const token = localStorage.getItem('token')
		const savedRole = localStorage.getItem('role')
		if (token) {
			setIsAuthenticated(true)
			setRole(savedRole)
			axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
		}
	}, [])

	const login = async (username: string, password: string) => {
		const response = await axios.post('/api/auth/login', {
			username,
			password,
		})
		const { token, role } = response.data
		localStorage.setItem('token', token)
		localStorage.setItem('role', role)
		document.cookie = `token=${token}; path=/; max-age=86400; SameSite=Lax`
		document.cookie = `role=${role}; path=/; max-age=86400; SameSite=Lax`
		axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
		setIsAuthenticated(true)
		setRole(role)
	}

	const logout = () => {
		localStorage.removeItem('token')
		localStorage.removeItem('role')
		document.cookie = 'token=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT'
		document.cookie = 'role=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT'
		delete axios.defaults.headers.common['Authorization']
		setIsAuthenticated(false)
		setRole(null)
	}

	return (
		<QueryClientProvider client={queryClient}>
			<AuthContext.Provider value={{ isAuthenticated, role, login, logout }}>
				<ThemeProvider theme={theme}>
					<CssBaseline />
					{children}
				</ThemeProvider>
			</AuthContext.Provider>
		</QueryClientProvider>
	)
}
