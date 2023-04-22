import MuiButton from '@mui/material/Button';
import { styled } from '@mui/material';

const Button = styled(MuiButton)({
  textTransform: 'unset',
  backgroundColor: 'white',
  borderRadius: '10px',
  color: 'black',
  '&:hover': {
    backgroundColor: '#4fc3f7',
  },
});

export default Button;