import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ProjectDetail = ({ match }) => {
    const [project, setProject] = useState(null);

    useEffect(() => {
        axios.get(`http://localhost:8083/api/v1/projects/${match.params.id}`)
            .then(response => {
                setProject(response.data);
            })
            .catch(error => console.error('Error fetching project:', error));
    }, [match.params.id]);

    if (!project) return <p>Loading...</p>;

    return (
        <div>
            <h1>Project Detail</h1>
            <p><strong>ID:</strong> {project.id}</p>
            <p><strong>Title:</strong> {project.title}</p>
            <p><strong>Description:</strong> {project.description}</p>
        </div>
    );
};

export default ProjectDetail;
