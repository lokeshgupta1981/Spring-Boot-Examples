import { Navigate, Route, Routes } from "react-router";
import { BrowserRouter as Router } from "react-router-dom";
import './App.css';
import HeaderComponent from './components/HeaderComponent';
import MyImagesComponent from './components/MyImagesComponent';
import UploadImageComponent from './components/UploadImageComponent';

function App() {
  return (
    <Router>
      <HeaderComponent />
      <div className='container'>
        <Routes>
          <Route path='/' element={<Navigate to='/my-images' />}></Route>
          <Route path='/my-images' element={<MyImagesComponent />}></Route>
          <Route path='/upload' element={<UploadImageComponent />}></Route>
        </Routes>
      </div>
    </Router>
  );
}

export default App;
