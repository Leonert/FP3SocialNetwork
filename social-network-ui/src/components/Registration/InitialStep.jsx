import { Box, Divider, Link, Typography } from '@mui/material';
import GoogleIcon from '@mui/icons-material/Google';

import React from 'react';
import Button from '../UI/Button';

const InitialStep = (props) => {
  return (
    <Box
      sx={{
        maxWidth: '364px',
        padding: '0 32px 38px',
        margin: 'auto',
        width: '100%',
      }}
    >
      <Typography sx={{ fontSize: '36px', marginTop: '10px', marginBottom: '20px' }}>Join Twitter now! </Typography>
      <Button
        startIcon={<GoogleIcon />}
        sx={{ maxWidth: '300px', width: '100%', marginBottom: '12px' }}
      >
        <Typography sx={{ fontSize: '14px' }}> Register with Google </Typography>
      </Button>
      <Divider sx={{ '&::after, &::before': { borderColor: 'rgba(255, 255, 255, 0.4)' }, marginBottom: '12px' }}>
        or
      </Divider>
      <Button
        onClick={props.onCreateAccount}
        sx={{ maxWidth: '300px', width: '100%', marginBottom: '30px' }}
      >
        <Typography sx={{ fontSize: '14px' }}> Registration </Typography>
      </Button>
      <Typography sx={{ fontSize: '14px' }}>
        Already have an account? <Link sx={{ cursor: 'pointer' }}>Sign in</Link>
      </Typography>
    </Box>
  );
};

export default InitialStep;