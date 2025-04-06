import React, { FC } from 'react';
import { Outlet } from 'react-router';

export const Home = () => {
    return (
        <>
            <Outlet />
        </>
    )
}