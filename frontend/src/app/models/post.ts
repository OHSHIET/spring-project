export interface Post {
  id?: number;
  content: string;
  title: string;
  createdAt: string;
  user: {
    id: number;
    username: string;
  };
}
