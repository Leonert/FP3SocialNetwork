import { TextField, withStyles } from '@mui/material';

export const PeopleSearchInput = withStyles((theme) => ({
  root: {
    '& .MuiOutlinedInput-root': {
      borderRadius: 30,
      border: '1px solid rgb(207, 217, 222)',
      padding: 0,
      paddingLeft: 15,
      marginLeft: 15,
      width: 385,
      '&.Mui-focused': {
        backgroundColor: theme.palette.secondary.light,
        '& fieldset': { borderWidth: 1, borderColor: theme.palette.primary.main },
        '& svg path': {
          fill: theme.palette.primary.main,
        },
      },
      '&:hover': {
        '& fieldset': { borderColor: 'transparent' },
      },
      '& fieldset': {
        borderColor: 'transparent',
        borderWidth: 1,
      },
      '& .MuiInputAdornment-root': {
        '& svg': {
          color: 'rgb(83, 100, 113)',
          height: '1.25em',
        },
      },
    },
    '& .MuiOutlinedInput-input': {
      padding: '12px 0px',
    },
  },
}))(TextField);
