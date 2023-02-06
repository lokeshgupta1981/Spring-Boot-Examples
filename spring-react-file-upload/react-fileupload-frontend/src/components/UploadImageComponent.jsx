import React, { Component } from 'react';
import { Navigate } from 'react-router-dom';
import FileService from '../services/FileService';

class UploadImageComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            files: null,
            fileUploaded: null,
            uploaderName: ''
        }
    }

    onFileChange = (event) => {
        this.setState({
            files: event.target.files
        });
    }

    onUploaderNameChange = (event) =>{
        this.setState({uploaderName : event.target.value});
    }
    onUpload = (event) => {
        event.preventDefault();
        const formData = new FormData();

        for (const key of Object.keys(this.state.files)) {
            formData.append('files', this.state.files[key]);
        }
        formData.append('name', this.state.uploaderName);

        FileService.uploadImage(formData).then((response) => {
            console.log(response.data);
            this.setState({ fileUploaded: true });
        }).catch(error => {
            console.log(error);
        });
    }

    render() {
        if(this.state.fileUploaded){
            return  <Navigate to="/my-images" replace={true} />;
        }

        return (
            <div className='row'>
                <div className='card col-md-6 offset-md-3 mt-5'>
                    <h3 className='text-center'>Upload Image</h3>
                    <div className='card-body'>
                        <form onSubmit={this.onUpload}>
                            <div>
                                <label>Select a file:</label>
                                <input className='mx-2' type='file' name='file' onChange={this.onFileChange} multiple></input>
                            </div>

                            <div className="mt-3">
                                <label>Uploader Name:</label>
                                <input className='mx-2' type='text' name='uploaderName' value={this.state.uploaderName} onChange={this.onUploaderNameChange}></input>
                            </div>
                            <button className='btn btn-success btn-sm mt-3' type='submit' disabled={!this.state.files || !this.state.uploaderName}>Upload</button>
                        </form>
                    </div>
                </div>
            </div>
        );
    }
}

export default UploadImageComponent;